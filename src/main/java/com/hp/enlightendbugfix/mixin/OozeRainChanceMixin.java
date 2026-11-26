package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.OozeRainChanceProcedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 OozeRainChanceProcedure 在客户端与全部维度每 tick 重复运行并每秒向全服广播 60+ 个 SavedData 同步包的网络风暴。
 */
@Mixin(value = OozeRainChanceProcedure.class, remap = false)
public abstract class OozeRainChanceMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$tickServerOnceWithoutPacketStorm(Event event, LevelAccessor world, CallbackInfo ci) {
        if (!EfBugfixConfig.OPTIMIZE_PACKET_STORMS.get()) {
            return;
        }
        // 仅在服务端末地维度（或无末地时跳过，因腐泥雨仅在末地生效；若直接调 execute 则按传入服务端维度处理）每刻执行一次
        if (!(world instanceof ServerLevel serverLevel)) {
            ci.cancel();
            return;
        }
        if (event != null && serverLevel.dimension() != Level.END) {
            ci.cancel();
            return;
        }

        EnlightenedEndModVariables.MapVariables mapVars = EnlightenedEndModVariables.MapVariables.get(serverLevel);
        EnlightenedEndModVariables.WorldVariables worldVars = EnlightenedEndModVariables.WorldVariables.get(serverLevel);
        RandomSource random = serverLevel.getRandom();
        boolean shouldSyncMap = false;

        if (mapVars.endraindelay == 0.0D) {
            mapVars.endraindelay = (double) Mth.nextInt(random, 24000, 96000);
        }

        if (mapVars.endraindo > mapVars.endraindelay) {
            if (worldVars.endrainchance > 24000.0D) {
                if (!mapVars.oozerain) {
                    mapVars.oozerain = true;
                    mapVars.endrainlast = (double) Mth.nextInt(random, 12000, 24000);
                    shouldSyncMap = true;
                }

                mapVars.endraintimetillend += 1.0D;
                if (mapVars.endraintimetillend > mapVars.endrainlast) {
                    mapVars.oozerain = false;
                    mapVars.endraintimetillend = 0.0D;
                    worldVars.endrainchance = 0.0D;
                    mapVars.endraindo = 0.0D;
                    mapVars.endraindelay = (double) Mth.nextInt(random, 24000, 96000);
                    shouldSyncMap = true;
                }
            } else {
                worldVars.endrainchance += 1.0D;
            }
        } else {
            mapVars.endraindo += 1.0D;
        }

        // 仅在腐泥雨启停状态真正切换时向客户端广播网络包，平时仅标记存档脏状态而不发包
        if (shouldSyncMap) {
            mapVars.syncData(serverLevel);
        } else if (serverLevel.getGameTime() % 200L == 0L) {
            mapVars.setDirty();
            worldVars.setDirty();
        }
        ci.cancel();
    }
}
