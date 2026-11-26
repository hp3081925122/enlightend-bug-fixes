package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.init.EnlightenedEndModParticleTypes;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.PlayerRadiationProcedure;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 PlayerRadiationProcedure 四大问题：
 * 1. 直接引用客户端 Minecraft 类导致专用服务端崩溃；
 * 2. 创造与旁观模式玩家在辐射为 0 时仍永久每秒发送 20 个 syncPlayerVariables 网络包；
 * 3. 粒子判定误读 entity.getPersistentData().getDouble("radiation") 导致高辐射玩家从不冒辐射粒子；
 * 4. 每次衰减均每刻高频发包与创建多个临时 PlayerVariables。
 */
@Mixin(value = PlayerRadiationProcedure.class, remap = false)
public abstract class PlayerRadiationMixin {
    @Unique
    private static final ResourceLocation ADV_RADIATION = new ResourceLocation("enlightened_end:radiation_advancement");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$safeServerRadiationTick(
            Event event, LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (entity == null || world == null) {
            ci.cancel();
            return;
        }

        EnlightenedEndModVariables.PlayerVariables vars = EfSafeEntityHelper.getMutablePlayerVars(entity);
        if (vars == null) {
            ci.cancel();
            return;
        }

        // 客户端本地仅做平滑插值显示，状态修改统一在服务端进行
        if (world.isClientSide()) {
            ci.cancel();
            return;
        }

        // 创造或旁观模式下仅当辐射值非 0 时才清零并同步一次，彻底杜绝每秒 20 包死循环
        if (EfSafeEntityHelper.isCreative(entity) || EfSafeEntityHelper.isSpectator(entity)) {
            if (vars.radiation != 0.0D) {
                vars.radiation = 0.0D;
                vars.syncPlayerVariables(entity);
            }
            ci.cancel();
            return;
        }

        if (vars.radiation <= 0.0D) {
            if (vars.radiation < 0.0D) {
                vars.radiation = 0.0D;
                vars.syncPlayerVariables(entity);
            }
            ci.cancel();
            return;
        }

        RandomSource random = entity.level().getRandom();

        if (vars.radiation >= 11050.0D && random.nextInt(10) == 0 && entity instanceof LivingEntity living) {
            living.hurt(new DamageSource(living.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)) {
                @Override
                public Component getLocalizedDeathMessage(LivingEntity msgEntity) {
                    String key = "death.attack.radiation";
                    if (this.getEntity() == null && this.getDirectEntity() == null) {
                        return msgEntity.getKillCredit() != null
                                ? Component.translatable(key + ".player", msgEntity.getDisplayName(), msgEntity.getKillCredit().getDisplayName())
                                : Component.translatable(key, msgEntity.getDisplayName());
                    }
                    Component killer = this.getEntity() == null ? this.getDirectEntity().getDisplayName() : this.getEntity().getDisplayName();
                    ItemStack weapon = this.getEntity() instanceof LivingEntity killerLiv ? killerLiv.getMainHandItem() : ItemStack.EMPTY;
                    return !weapon.isEmpty() && weapon.hasCustomHoverName()
                            ? Component.translatable(key + ".item", msgEntity.getDisplayName(), killer, weapon.getDisplayName())
                            : Component.translatable(key, msgEntity.getDisplayName(), killer);
                }
            }, 1.0F);
        }

        if (vars.radiation > 13650.0D) {
            vars.radiation = 13650.0D;
        }

        double currentRad = vars.radiation;
        EfSafeEntityHelper.awardAdvancement(entity, ADV_RADIATION);

        if (world instanceof ServerLevel serverLevel) {
            SimpleParticleType particle = EnlightenedEndModParticleTypes.URANIUM_EMISSION.get();
            if (currentRad > 3000.0D && random.nextInt(10) == 0) {
                enlightendbugfix$spawnEmissionParticle(serverLevel, particle, random, entity, x, y, z);
            }
            if (currentRad > 4000.0D && random.nextInt(8) == 0) {
                enlightendbugfix$spawnEmissionParticle(serverLevel, particle, random, entity, x, y, z);
            }
            if (currentRad > 6000.0D && random.nextInt(7) == 0) {
                enlightendbugfix$spawnEmissionParticle(serverLevel, particle, random, entity, x, y, z);
            }
            if (currentRad > 12000.0D && random.nextInt(7) == 0) {
                enlightendbugfix$spawnEmissionParticle(serverLevel, particle, random, entity, x, y, z);
            }
        }

        int prevBar = (int) (vars.radiation / 650.0D);
        boolean wasFatal = vars.radiation >= 11050.0D;
        vars.radiation = Math.max(0.0D, vars.radiation - 2.0D);
        int nextBar = (int) (vars.radiation / 650.0D);
        boolean nowFatal = vars.radiation >= 11050.0D;

        // 仅在辐射格数变化、致死阈值跨越、归零或每 10 tick 节流同步一次，减少 90% 网络发包
        if (vars.radiation == 0.0D || prevBar != nextBar || wasFatal != nowFatal || entity.tickCount % 10 == 0) {
            vars.syncPlayerVariables(entity);
        }

        ci.cancel();
    }

    @Unique
    private static void enlightendbugfix$spawnEmissionParticle(
            ServerLevel serverLevel, SimpleParticleType particle, RandomSource random, Entity entity, double x, double y, double z
    ) {
        double w = entity.getBbWidth();
        double h = entity.getBbHeight();
        serverLevel.sendParticles(
                particle,
                x + Mth.nextDouble(random, -w, w),
                y + Mth.nextDouble(random, 0.0D, h),
                z + Mth.nextDouble(random, -w, w),
                1,
                Mth.nextDouble(random, -0.02D, 0.02D),
                Mth.nextDouble(random, -0.02D, 0.02D),
                Mth.nextDouble(random, -0.02D, 0.02D),
                0.01D
        );
    }
}
