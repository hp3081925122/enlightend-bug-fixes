package com.hp.enlightendbugfix.mixin.client;

import com.mojang.blaze3d.shaders.FogShape;
import net.mcreator.enlightened_end.procedures.EndRenderingProcedure;
import net.mcreator.enlightened_end.procedures.IsFreezingEndProcedure;
import net.mcreator.enlightened_end.procedures.IsOozeSeepsProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 EndRenderingProcedure 在主世界、下界及玩家失明状态下仍无条件调用 event.setCanceled(true) 破坏地形迷雾的问题。
 */
@Mixin(value = EndRenderingProcedure.class, remap = false)
public abstract class EndRenderingFogMixin {
    @Inject(method = "renderFog", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$onlyCancelEndTerrainFog(ViewportEvent.RenderFog event, CallbackInfo ci) {
        if (event.getMode() == FogRenderer.FogMode.FOG_TERRAIN) {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            Entity entity = event.getCamera().getEntity();
            if (clientLevel != null && entity != null && clientLevel.dimension() == Level.END) {
                if (entity instanceof LivingEntity living
                        && (living.hasEffect(MobEffects.BLINDNESS) || living.hasEffect(MobEffects.DARKNESS))) {
                    ci.cancel();
                    return;
                }

                double x = entity.getX();
                double y = entity.getY();
                double z = entity.getZ();
                if (IsFreezingEndProcedure.execute(clientLevel, x, y, z)) {
                    event.setNearPlaneDistance(0.0F);
                    event.setFarPlaneDistance(50.0F);
                } else if (IsOozeSeepsProcedure.execute(clientLevel, x, y, z)) {
                    event.setNearPlaneDistance(0.0F);
                    event.setFarPlaneDistance(125.0F);
                } else {
                    event.setNearPlaneDistance(0.0F);
                    event.setFarPlaneDistance(200.0F);
                }
                event.setFogShape(FogShape.SPHERE);
                event.setCanceled(true);
            }
        }
        ci.cancel();
    }
}
