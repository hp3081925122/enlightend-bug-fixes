package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.mcreator.enlightened_end.network.DashingLeggingsAbilityMessage;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 加固 C2S 冲刺护腿按键网络包（DashingLeggingsAbilityMessage）：
 * 服务端严格校验发送者非空、存活、非旁观者、按键类型合法且确实穿戴了冲刺护腿，防止伪造发包绕过状态校验。
 */
@Mixin(value = DashingLeggingsAbilityMessage.class, remap = false)
public abstract class DashingLeggingsMessageGuardMixin {
    @Inject(method = "pressAction", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$validateDashPacketSender(Player entity, int type, int pressedms, CallbackInfo ci) {
        if (entity == null || !entity.isAlive() || entity.isSpectator() || type != 0) {
            ci.cancel();
            return;
        }
        if (entity.getItemBySlot(EquipmentSlot.LEGS).getItem() != EnlightenedEndModItems.DASHING_LEGGINGS_LEGGINGS.get()) {
            ci.cancel();
        }
    }
}
