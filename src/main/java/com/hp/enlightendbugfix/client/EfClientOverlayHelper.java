package com.hp.enlightendbugfix.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiEvent;

/**
 * 客户端 HUD 覆盖层复用渲染辅助类：
 * 仅在显示条件成立后才切换 OpenGL 深度与混合状态，消除 8 个 HUD 覆盖层每帧空跑渲染状态切换与 ResourceLocation 分配。
 */
public final class EfClientOverlayHelper {
    private EfClientOverlayHelper() {
    }

    public static void renderFullscreen(RenderGuiEvent.Pre event, ResourceLocation texture) {
        int w = event.getWindow().getGuiScaledWidth();
        int h = event.getWindow().getGuiScaledHeight();
        beginOverlayState();
        event.getGuiGraphics().blit(texture, 0, 0, 0.0F, 0.0F, w, h, w, h);
        endOverlayState();
    }

    public static void renderIcon(RenderGuiEvent.Pre event, ResourceLocation texture, int x, int y, int width, int height) {
        beginOverlayState();
        event.getGuiGraphics().blit(texture, x, y, 0.0F, 0.0F, width, height, width, height);
        endOverlayState();
    }

    private static void beginOverlayState() {
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
    }

    private static void endOverlayState() {
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
