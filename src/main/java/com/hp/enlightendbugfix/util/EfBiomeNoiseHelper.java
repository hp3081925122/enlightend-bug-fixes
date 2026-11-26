package com.hp.enlightendbugfix.util;

import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.SetBiomeSeedProcedure;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

/**
 * 末地子群系 SimplexNoise 种子稳定化与双端同步辅助类。
 * 保证服务端世界生成、传送门切换以及多人客户端迷雾判定始终使用同一确定性世界种子与同一随机数算法。
 */
public final class EfBiomeNoiseHelper {
    private static volatile long cachedServerSeed = Long.MIN_VALUE;
    private static volatile SimplexNoise cachedServerNoise;

    private static volatile long cachedClientSeed = Long.MIN_VALUE;
    private static volatile SimplexNoise cachedClientNoise;

    private static final SimplexNoise FALLBACK_NOISE = new SimplexNoise(RandomSource.create(1L));

    private EfBiomeNoiseHelper() {
    }

    /**
     * 将 64 位世界种子拆分写入原模组已有的两个未使用的双精度地图变量字段，实现无额外协议包的客户端同步。
     */
    public static void writeSeedToMapVariables(EnlightenedEndModVariables.MapVariables mapVars, long seed) {
        if (mapVars == null) {
            return;
        }
        int high = (int) (seed >>> 32);
        int low = (int) seed;
        mapVars.enlightendbiomeseed = (double) high;
        mapVars.dsad = (double) low;
    }

    public static boolean hasSyncedSeed(EnlightenedEndModVariables.MapVariables mapVars) {
        return mapVars != null && (mapVars.enlightendbiomeseed != 0.0D || mapVars.dsad != 0.0D);
    }

    public static long readSeedFromMapVariables(EnlightenedEndModVariables.MapVariables mapVars) {
        long high = (long) (int) mapVars.enlightendbiomeseed;
        long low = ((long) (int) mapVars.dsad) & 0xFFFFFFFFL;
        return (high << 32) | low;
    }

    public static SimplexNoise updateServerSeed(long seed) {
        SimplexNoise current = cachedServerNoise;
        if (current == null || cachedServerSeed != seed) {
            synchronized (EfBiomeNoiseHelper.class) {
                if (cachedServerNoise == null || cachedServerSeed != seed) {
                    cachedServerNoise = new SimplexNoise(RandomSource.create(seed));
                    cachedServerSeed = seed;
                }
                current = cachedServerNoise;
            }
        }
        SetBiomeSeedProcedure.generator = current;
        return current;
    }

    public static SimplexNoise getNoise(LevelAccessor world) {
        if (world != null && !world.isClientSide()) {
            MinecraftServer server = world.getServer();
            if (server == null && world instanceof ServerLevelAccessor sla) {
                server = sla.getLevel().getServer();
            }
            if (server != null) {
                long seed = server.getWorldData().worldGenOptions().seed();
                return updateServerSeed(seed);
            }
            if (world instanceof WorldGenLevel wgl) {
                long regionSeed = wgl.getSeed();
                return updateServerSeed(regionSeed);
            }
        }

        if (world != null && world.isClientSide()) {
            EnlightenedEndModVariables.MapVariables mapVars = EnlightenedEndModVariables.MapVariables.get(world);
            if (hasSyncedSeed(mapVars)) {
                long syncedSeed = readSeedFromMapVariables(mapVars);
                SimplexNoise clientNoise = cachedClientNoise;
                if (clientNoise == null || cachedClientSeed != syncedSeed) {
                    clientNoise = new SimplexNoise(RandomSource.create(syncedSeed));
                    cachedClientNoise = clientNoise;
                    cachedClientSeed = syncedSeed;
                }
                return clientNoise;
            }
            if (cachedServerNoise != null) {
                return cachedServerNoise;
            }
        }

        SimplexNoise existing = SetBiomeSeedProcedure.generator;
        return existing != null ? existing : FALLBACK_NOISE;
    }
}
