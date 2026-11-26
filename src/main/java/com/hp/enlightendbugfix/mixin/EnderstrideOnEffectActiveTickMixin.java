package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.init.EnlightenedEndModParticleTypes;
import net.mcreator.enlightened_end.procedures.EnderstrideOnEffectActiveTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 EnderstrideOnEffectActiveTickProcedure 引用客户端 Minecraft 类导致专用服务端崩溃及单次瞬移重复执行 18 次射线追踪的问题。
 */
@Mixin(value = EnderstrideOnEffectActiveTickProcedure.class, remap = false)
public abstract class EnderstrideOnEffectActiveTickMixin {
    @Unique
    private static final ResourceLocation CHORUS_TELEPORT_SOUND = new ResourceLocation("item.chorus_fruit.teleport");
    @Unique
    private static final ResourceLocation ADV_ENDERSTRIDE = new ResourceLocation("enlightened_end:enderstride_advancement");

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$serverSafeEnderstride(
            LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (entity == null || world == null || EfSafeEntityHelper.isSpectator(entity)) {
            ci.cancel();
            return;
        }

        RandomSource random = entity.level().getRandom();
        if (random.nextInt(120) != 0) {
            ci.cancel();
            return;
        }

        Vec3 eye = entity.getEyePosition(1.0F);
        Vec3 reach = eye.add(entity.getViewVector(1.0F).scale(5.0D));
        BlockHitResult hit = entity.level().clip(new ClipContext(eye, reach, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
        BlockPos targetPos = hit.getBlockPos();

        boolean validTarget = world.isEmptyBlock(targetPos)
                || world.getFluidState(targetPos).createLegacyBlock().getBlock() == EnlightenedEndModBlocks.OOZE_FLUID.get();
        if (!validTarget) {
            ci.cancel();
            return;
        }

        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(CHORUS_TELEPORT_SOUND);
        if (world instanceof Level level && sound != null) {
            if (!level.isClientSide()) {
                level.playSound(null, targetPos, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else {
                level.playLocalSound(targetPos.getX(), targetPos.getY(), targetPos.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F, false);
            }
        }

        if (world instanceof ServerLevel serverLevel) {
            SimpleParticleType smoke = EnlightenedEndModParticleTypes.OOZE_SMOKE.get();
            enlightendbugfix$spawnSmokeBurst(serverLevel, smoke, random, entity, x, y, z);

            double tx = targetPos.getX() + 0.5D;
            double ty = targetPos.getY();
            double tz = targetPos.getZ() + 0.5D;
            entity.teleportTo(tx, ty, tz);
            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.teleport(tx, ty, tz, entity.getYRot(), entity.getXRot());
            }

            enlightendbugfix$spawnSmokeBurst(serverLevel, smoke, random, entity, entity.getX(), entity.getY(), entity.getZ());
            EfSafeEntityHelper.awardAdvancement(entity, ADV_ENDERSTRIDE);
        }

        ci.cancel();
    }

    @Unique
    private static void enlightendbugfix$spawnSmokeBurst(
            ServerLevel serverLevel, SimpleParticleType smoke, RandomSource random, Entity entity, double x, double y, double z
    ) {
        double halfW = entity.getBbWidth() * -0.5D;
        double fullW = entity.getBbWidth();
        double fullH = entity.getBbHeight();
        for (int i = 0; i < 7; i++) {
            serverLevel.sendParticles(
                    smoke,
                    x + Mth.nextDouble(random, halfW, fullW),
                    y + Mth.nextDouble(random, 0.0D, fullH),
                    z + Mth.nextDouble(random, halfW, fullW),
                    1, 0.1D, 0.1D, 0.1D, 0.1D
            );
        }
    }
}
