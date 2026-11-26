package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.DashingLeggingsLeggingsTickEventProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复冲刺护腿冷却期间每 tick 分配临时 PlayerVariables 并每秒发 20 次同步包的问题。
 */
@Mixin(value = DashingLeggingsLeggingsTickEventProcedure.class, remap = false)
public abstract class DashingLeggingsTickMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$cleanCooldownTick(Entity entity, CallbackInfo ci) {
        if (entity == null) {
            ci.cancel();
            return;
        }
        EnlightenedEndModVariables.PlayerVariables vars = EfSafeEntityHelper.getMutablePlayerVars(entity);
        if (vars != null && vars.dashing_cooldown >= 1.0D) {
            vars.dashing_cooldown = Math.max(0.0D, vars.dashing_cooldown - 1.0D);
            if (!entity.level().isClientSide() && vars.dashing_cooldown == 0.0D) {
                vars.syncPlayerVariables(entity);
            }
        }
        ci.cancel();
    }
}
