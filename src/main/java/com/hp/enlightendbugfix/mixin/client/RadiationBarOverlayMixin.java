package com.hp.enlightendbugfix.mixin.client;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mcreator.enlightened_end.client.screens.RadiationBarOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 优化辐射条 HUD 渲染：
 * 1. 无辐射（radiation < 650）时直接跳过，避免每帧无意义切换 OpenGL 深度/混合状态；
 * 2. 将每帧 20 次临时 PlayerVariables 分配与 20 次 ResourceLocation 解析降为 0 分配。
 */
@Mixin(value = RadiationBarOverlay.class, remap = false)
public abstract class RadiationBarOverlayMixin {
    @Unique
    private static final ResourceLocation[] RAD_TEXTURES = new ResourceLocation[20];

    static {
        for (int i = 0; i < 20; i++) {
            RAD_TEXTURES[i] = new ResourceLocation("enlightened_end:textures/screens/" + (i + 1) + "_rads.png");
        }
    }

    @Inject(method = "eventHandler", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$zeroAllocRadiationHud(RenderGuiEvent.Pre event, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            ci.cancel();
            return;
        }

        double radiation = EfSafeEntityHelper.getReadOnlyPlayerVars(player).radiation;
        if (radiation < 650.0D) {
            ci.cancel();
            return;
        }

        int maxIdx = Math.min(19, ((int) (radiation / 650.0D)) - 1);
        int posX = event.getWindow().getGuiScaledWidth() / 2;
        int posY = event.getWindow().getGuiScaledHeight() / 2;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i <= maxIdx; i++) {
            event.getGuiGraphics().blit(RAD_TEXTURES[i], posX - 90, posY + 77, 0.0F, 0.0F, 99, 9, 99, 9);
        }

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        ci.cancel();
    }
}
