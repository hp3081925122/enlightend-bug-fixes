package com.hp.enlightendbugfix.mixin.client;

import com.hp.enlightendbugfix.client.EfClientOverlayHelper;
import net.mcreator.enlightened_end.client.screens.OozeOverlayOverlay;
import net.mcreator.enlightened_end.procedures.OozeOverlayDisplayOverlayIngameProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 腐泥液体覆盖层：条件不成立时在切换 OpenGL 状态前直接短路退出，并缓存贴图路径。
 */
@Mixin(value = OozeOverlayOverlay.class, remap = false)
public abstract class OozeOverlayMixin {
    @Unique
    private static final ResourceLocation TEXTURE = new ResourceLocation("enlightened_end:textures/screens/ooze_overlay.png");

    @Inject(method = "eventHandler", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$shortCircuitOozeOverlay(RenderGuiEvent.Pre event, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null && OozeOverlayDisplayOverlayIngameProcedure.execute(player.level(), player.getX(), player.getY(), player.getZ(), player)) {
            EfClientOverlayHelper.renderFullscreen(event, TEXTURE);
        }
        ci.cancel();
    }
}
