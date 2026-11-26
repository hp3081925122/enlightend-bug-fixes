package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.RadiationTickProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 RadiationTickProcedure 每 tick 创建临时 PlayerVariables 并高频发包同步 radiated 倒计时的问题。
 */
@Mixin(value = RadiationTickProcedure.class, remap = false)
public abstract class RadiationTickMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$cleanRadiatedCountdown(Event event, Entity entity, CallbackInfo ci) {
        if (entity == null) {
            ci.cancel();
            return;
        }
        EnlightenedEndModVariables.PlayerVariables vars = EfSafeEntityHelper.getMutablePlayerVars(entity);
        if (vars != null && vars.radiated > 0.0D) {
            vars.radiated = Math.max(0.0D, vars.radiated - 1.0D);
            if (!entity.level().isClientSide() && vars.radiated == 0.0D) {
                vars.syncPlayerVariables(entity);
            }
        }
        ci.cancel();
    }
}
