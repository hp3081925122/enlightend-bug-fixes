package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import com.hp.enlightendbugfix.util.EfBiomeNoiseHelper;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.DefaultBiomeSeedProcedure;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 DefaultBiomeSeedProcedure 在客户端加载世界时因 getServer() 为空把静态群系种子覆盖成 1L 的严重错乱问题。
 */
@Mixin(value = DefaultBiomeSeedProcedure.class, remap = false)
public abstract class DefaultBiomeSeedMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$safeInitBiomeSeed(Event event, LevelAccessor world, CallbackInfo ci) {
        if (!EfBugfixConfig.FIX_BIOME_NOISE_SEED.get()) {
            return;
        }
        if (world != null && !world.isClientSide()) {
            MinecraftServer server = world.getServer();
            if (server != null) {
                long worldSeed = server.getWorldData().worldGenOptions().seed();
                EfBiomeNoiseHelper.updateServerSeed(worldSeed);
                EnlightenedEndModVariables.MapVariables mapVars = EnlightenedEndModVariables.MapVariables.get(world);
                if (mapVars != null) {
                    EfBiomeNoiseHelper.writeSeedToMapVariables(mapVars, worldSeed);
                    mapVars.setDirty();
                }
            }
        }
        ci.cancel();
    }
}
