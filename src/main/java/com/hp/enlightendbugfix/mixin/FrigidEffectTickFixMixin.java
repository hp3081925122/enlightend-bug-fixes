package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.EnlightenedEndMod;
import net.mcreator.enlightened_end.procedures.FrigidOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 修复 FrigidOnEffectActiveTickProcedure 在 MobEffect#applyEffectTick 期间双端直接调用
 * LivingEntity#removeEffect(FRIGID) 触发 tickEffects() ConcurrentModificationException 中断其余药水结算的问题（MCR-55）。
 */
@Mixin(FrigidOnEffectActiveTickProcedure.class)
public abstract class FrigidEffectTickFixMixin {

    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z"
            )
    )
    private static boolean enlightendbugfix$deferFrigidRemove(LivingEntity living, MobEffect effect) {
        if (living == null || effect == null || living.level().isClientSide() || !living.hasEffect(effect)) {
            return false;
        }
        EnlightenedEndMod.queueServerWork(1, () -> {
            if (living.isAlive() && living.hasEffect(effect)) {
                living.removeEffect(effect);
            }
        });
        return true;
    }
}
