package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.entity.FlashBombEntity;
import net.mcreator.enlightened_end.init.EnlightenedEndModMobEffects;
import net.mcreator.enlightened_end.init.EnlightenedEndModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复闪光弹（FlashBombEntity）0 伤害命中实体后弹开不消失导致无限连续致盲闪屏，以及发射器发射时无投手不触发爆炸的问题。
 */
@Mixin(FlashBombEntity.class)
public abstract class FlashBombEntityMixin extends AbstractArrow {
    @Unique
    private static final ResourceLocation FLASH_BOMB_SOUND_ID = new ResourceLocation("enlightened_end:flash_bomb_blow");

    protected FlashBombEntityMixin(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$detonateOnEntityAndDiscard(EntityHitResult hitResult, CallbackInfo ci) {
        if (hitResult.getEntity() != this) {
            this.enlightendbugfix$detonateAt(this.getX(), this.getY(), this.getZ());
            if (!this.level().isClientSide()) {
                this.discard();
            }
        }
        ci.cancel();
    }

    @Inject(method = "onHitBlock", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$detonateOnBlockEvenWithoutOwner(BlockHitResult hitResult, CallbackInfo ci) {
        super.onHitBlock(hitResult);
        BlockPos pos = hitResult.getBlockPos();
        this.enlightendbugfix$detonateAt(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        if (!this.level().isClientSide()) {
            this.discard();
        }
        ci.cancel();
    }

    @Unique
    private void enlightendbugfix$detonateAt(double x, double y, double z) {
        Level level = this.level();
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(FLASH_BOMB_SOUND_ID);
        if (sound != null) {
            if (!level.isClientSide()) {
                level.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.NEUTRAL, 3.0F, 1.0F);
            } else {
                level.playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, 3.0F, 1.0F, false);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.getRandom();
            SimpleParticleType particle = EnlightenedEndModParticleTypes.FLASH_BOMB_FLASH.get();
            for (int i = 0; i < 35; i++) {
                serverLevel.sendParticles(
                        particle,
                        x + Mth.nextDouble(random, -4.0D, 4.0D),
                        y + Mth.nextDouble(random, -4.0D, 4.0D),
                        z + Mth.nextDouble(random, -4.0D, 4.0D),
                        1,
                        Mth.nextDouble(random, -0.05D, 0.05D),
                        Mth.nextDouble(random, -0.05D, 0.05D),
                        Mth.nextDouble(random, -0.05D, 0.05D),
                        0.0D
                );
            }

            Vec3 center = new Vec3(x, y, z);
            for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(4.0D))) {
                target.addEffect(new MobEffectInstance(EnlightenedEndModMobEffects.DAZED.get(), 60, 0, false, true));
            }
        }
    }
}
