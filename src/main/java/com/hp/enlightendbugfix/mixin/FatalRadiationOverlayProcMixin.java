package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.procedures.FatalRadiationOverlayDisplayOverlayIngameProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复致死辐射覆盖层判定过程直接引用客户端 Minecraft 类及重复创建临时 PlayerVariables 的问题。
 */
@Mixin(value = FatalRadiationOverlayDisplayOverlayIngameProcedure.class, remap = false)
public abstract class FatalRadiationOverlayProcMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$safeCheckFatalRadiation(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity == null || EfSafeEntityHelper.isSpectator(entity)) {
            cir.setReturnValue(false);
            return;
        }
        cir.setReturnValue(EfSafeEntityHelper.getReadOnlyPlayerVars(entity).radiation >= 11050.0D);
    }
}
