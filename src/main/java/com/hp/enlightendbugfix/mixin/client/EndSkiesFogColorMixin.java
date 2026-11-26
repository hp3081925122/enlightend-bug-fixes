package com.hp.enlightendbugfix.mixin.client;

import com.mojang.blaze3d.shaders.FogShape;
import net.mcreator.enlightened_end.procedures.EndSkiesProcedure;
import net.mcreator.enlightened_end.procedures.IsCongealedForestProcedure;
import net.mcreator.enlightened_end.procedures.IsExogasBeltProcedure;
import net.mcreator.enlightened_end.procedures.IsFreezingEndProcedure;
import net.mcreator.enlightened_end.procedures.IsOozeSeepsProcedure;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 EndSkiesProcedure 在纯计算迷雾颜色的 ComputeFogColor 事件中强行开启 SRC_ALPHA/ONE 加法混合且永不还原导致的客户端渲染状态污染。
 */
@Mixin(value = EndSkiesProcedure.class, remap = false)
public abstract class EndSkiesFogColorMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/resources/ResourceKey;Lnet/minecraftforge/client/event/ViewportEvent;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$computeEndFogColorWithoutBlendLeak(
            Event event,
            LevelAccessor world,
            double x,
            double y,
            double z,
            ResourceKey<Level> dimension,
            ViewportEvent viewport,
            CallbackInfo ci
    ) {
        if (dimension == Level.END && viewport != null) {
            if (viewport instanceof ViewportEvent.RenderFog renderFog) {
                renderFog.setFogShape(FogShape.SPHERE);
            }
            if (viewport instanceof ViewportEvent.ComputeFogColor fogColor) {
                if (IsCongealedForestProcedure.execute(world, x, y, z)) {
                    fogColor.setRed(0.16078432F);
                    fogColor.setGreen(0.03529412F);
                    fogColor.setBlue(0.09411765F);
                } else if (IsOozeSeepsProcedure.execute(world, x, y, z)) {
                    fogColor.setRed(0.06666667F);
                    fogColor.setGreen(0.019607844F);
                    fogColor.setBlue(0.078431375F);
                } else if (IsExogasBeltProcedure.execute(world, x, y, z)) {
                    fogColor.setRed(0.07450981F);
                    fogColor.setGreen(0.078431375F);
                    fogColor.setBlue(0.019607844F);
                } else if (IsFreezingEndProcedure.execute(world, x, y, z)) {
                    fogColor.setRed(0.019607844F);
                    fogColor.setGreen(0.023529412F);
                    fogColor.setBlue(0.078431375F);
                } else {
                    fogColor.setRed(0.078431375F);
                    fogColor.setGreen(0.0627451F);
                    fogColor.setBlue(0.101960786F);
                }
            }
        }
        ci.cancel();
    }
}
