package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.EnlightenedEndMod;
import net.mcreator.enlightened_end.init.EnlightenedEndModMobEffects;
import net.mcreator.enlightened_end.procedures.CeruleanAnglerfishOnEntityTickUpdateProcedure;
import net.mcreator.enlightened_end.procedures.HeavyGutActiveTickConditionProcedure;
import net.mcreator.enlightened_end.procedures.VoidsGraspOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复实体与状态效果每 tick 无条件调用 removeEffect 触发事件风暴，
 * 以及在 MobEffect#applyEffectTick 遍历期直接 removeEffect 触发 ConcurrentModificationException
 * 中断该实体本 tick 剩余药水效果结算的问题（MCR-27 / MCR-55）。
 */
@Mixin(value = {
        CeruleanAnglerfishOnEntityTickUpdateProcedure.class,
        HeavyGutActiveTickConditionProcedure.class,
        VoidsGraspOnEffectActiveTickProcedure.class
}, remap = false)
public abstract class RemoveEffectGuardsMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$guardHasEffectBeforeRemove(Entity entity, CallbackInfo ci) {
        if (!(entity instanceof LivingEntity living)) {
            ci.cancel();
            return;
        }

        // Stalker（CeruleanAnglerfish）免疫负重
        if (living.getClass().getSimpleName().equals("StalkerEntity")) {
            enlightendbugfix$deferRemoveIfPresent(living, EnlightenedEndModMobEffects.BURDENED.get());
            ci.cancel();
            return;
        }

        // 若当前正持有负重（Burdened / VoidsGrasp）效果，走负重下沉分支；否则走活力（Vitality / HeavyGut）净化分支
        MobEffect burdened = EnlightenedEndModMobEffects.BURDENED.get();
        if (living.hasEffect(burdened)) {
            enlightendbugfix$deferRemoveIfPresent(living, MobEffects.SLOW_FALLING);
            enlightendbugfix$deferRemoveIfPresent(living, MobEffects.JUMP);
            enlightendbugfix$deferRemoveIfPresent(living, MobEffects.LEVITATION);
            if (!(living instanceof Player player && player.getAbilities().instabuild)) {
                MobEffectInstance inst = living.getEffect(burdened);
                int amp = inst != null ? inst.getAmplifier() : 0;
                Vec3 dm = living.getDeltaMovement();
                living.setDeltaMovement(dm.x(), dm.y() - (double) ((amp + 1) / 5), dm.z());
            }
        } else {
            enlightendbugfix$deferRemoveIfPresent(living, MobEffects.WITHER);
            enlightendbugfix$deferRemoveIfPresent(living, MobEffects.CONFUSION);
            enlightendbugfix$deferRemoveIfPresent(living, MobEffects.POISON);
        }

        ci.cancel();
    }

    @Unique
    private static void enlightendbugfix$deferRemoveIfPresent(LivingEntity living, MobEffect effect) {
        if (effect == null || living.level().isClientSide() || !living.hasEffect(effect)) {
            return;
        }
        // 推迟 1 tick 在 LivingEntity#tickEffects 遍历结束后于服务端安全移除，避免并发修改中断其余药水结算
        EnlightenedEndMod.queueServerWork(1, () -> {
            if (living.isAlive() && living.hasEffect(effect)) {
                living.removeEffect(effect);
            }
        });
    }
}
