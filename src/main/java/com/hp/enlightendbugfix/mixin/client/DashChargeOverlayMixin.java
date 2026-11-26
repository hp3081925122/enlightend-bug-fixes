package com.hp.enlightendbugfix.mixin.client;

import com.hp.enlightendbugfix.client.EfClientOverlayHelper;
import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.client.screens.DashChargeOverlay;
import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 冲刺护腿充能 HUD：未穿戴护腿时在切换 OpenGL 状态前直接短路，且单次零分配读取冷却值并复用缓存贴图。
 */
@Mixin(value = DashChargeOverlay.class, remap = false)
public abstract class DashChargeOverlayMixin {
    @Unique
    private static final ResourceLocation DASH_0 = new ResourceLocation("enlightened_end:textures/screens/dash_charge_0.png");
    @Unique
    private static final ResourceLocation DASH_1 = new ResourceLocation("enlightened_end:textures/screens/dash_charge_1.png");
    @Unique
    private static final ResourceLocation DASH_2 = new ResourceLocation("enlightened_end:textures/screens/dash_charge_2.png");
    @Unique
    private static final ResourceLocation DASH_3 = new ResourceLocation("enlightened_end:textures/screens/dash_charge_3.png");

    @Inject(method = "eventHandler", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$shortCircuitDashChargeOverlay(RenderGuiEvent.Pre event, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.getItemBySlot(EquipmentSlot.LEGS).getItem() != EnlightenedEndModItems.DASHING_LEGGINGS_LEGGINGS.get()) {
            ci.cancel();
            return;
        }
        double cooldown = EfSafeEntityHelper.getReadOnlyPlayerVars(player).dashing_cooldown;
        ResourceLocation texture = null;
        if (cooldown > 72.0D && cooldown <= 120.0D) {
            texture = DASH_0;
        } else if (cooldown > 24.0D && cooldown <= 72.0D) {
            texture = DASH_1;
        } else if (cooldown > 0.0D && cooldown <= 24.0D) {
            texture = DASH_2;
        } else if (cooldown == 0.0D) {
            texture = DASH_3;
        }
        if (texture != null) {
            int posX = event.getWindow().getGuiScaledWidth() / 2;
            int posY = event.getWindow().getGuiScaledHeight() / 2;
            EfClientOverlayHelper.renderIcon(event, texture, posX + 99, posY + 77, 16, 16);
        }
        ci.cancel();
    }
}
