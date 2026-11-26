package com.hp.enlightendbugfix.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 终末之光（Enlightend）修复补丁配置项。
 */
public final class EfBugfixConfig {
    public static final ForgeConfigSpec COMMON_SPEC;

    public static final ForgeConfigSpec.BooleanValue DISABLE_PATREON_CHAT_MESSAGE;
    public static final ForgeConfigSpec.BooleanValue FIX_BIOME_NOISE_SEED;
    public static final ForgeConfigSpec.BooleanValue OPTIMIZE_PACKET_STORMS;
    public static final ForgeConfigSpec.BooleanValue FIX_GLOBAL_MUSIC_STOPSOUND;
    public static final ForgeConfigSpec.BooleanValue FIX_NUCLEAR_FURNACE;
    public static final ForgeConfigSpec.BooleanValue FIX_VOIDSTEEL_PICKAXE_RAYTRACE;
    public static final ForgeConfigSpec.DoubleValue RADIATION_SCAN_RADIUS;
    public static final ForgeConfigSpec.IntValue NUCLEAR_FURNACE_RADIATION_INTERVAL;
    public static final ForgeConfigSpec.IntValue OOZE_RAIN_PARTICLES_PER_TICK;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("通用修复与性能优化配置").push("general");

        DISABLE_PATREON_CHAT_MESSAGE = builder
                .comment("是否禁用玩家首次进入末地时在公屏弹出的 Patreon 赞助提示消息")
                .define("disablePatreonChatMessage", true);

        FIX_BIOME_NOISE_SEED = builder
                .comment("是否修复末地子群系 SimplexNoise 种子在客户端加载和跨维度传送时被重置或混用不同随机数算法的问题")
                .define("fixBiomeNoiseSeed", true);

        OPTIMIZE_PACKET_STORMS = builder
                .comment("是否修复 OozeRainChance 全服每刻广播 SavedData 以及玩家每刻高频同步 PlayerVariables 的网络发包风暴")
                .define("optimizePacketStorms", true);

        FIX_GLOBAL_MUSIC_STOPSOUND = builder
                .comment("是否修复末地背景音乐触发时使用 stopsound @a 强行掐断全服所有玩家音乐的问题")
                .define("fixGlobalMusicStopsound", true);

        FIX_NUCLEAR_FURNACE = builder
                .comment("是否修复核熔炉吞第 2 根铱棒、空烧燃料、0 tick 瞬烧首件物品及空闲无限发方块更新包的问题")
                .define("fixNuclearFurnace", true);

        FIX_VOIDSTEEL_PICKAXE_RAYTRACE = builder
                .comment("是否修复虚空钢镐（Adamantite Pickaxe）右键技能错取玩家脚下坐标且可破坏基岩/不可破坏方块的问题")
                .define("fixVoidsteelPickaxeRaytrace", true);

        builder.pop();

        builder.comment("实体扫描与客户端粒子性能调节").push("performance");

        RADIATION_SCAN_RADIUS = builder
                .comment("辐射方块与贫铀方块扫描周围实体的半径（原模组默认为 8.0 格）")
                .defineInRange("radiationScanRadius", 8.0D, 1.0D, 16.0D);

        NUCLEAR_FURNACE_RADIATION_INTERVAL = builder
                .comment("核熔炉燃烧时释放辐射扫描的间隔刻数（原模组每 1 tick 扫一次，其余辐射矿石为 5 tick）")
                .defineInRange("nuclearFurnaceRadiationInterval", 5, 1, 40);

        OOZE_RAIN_PARTICLES_PER_TICK = builder
                .comment("末地腐泥雨（Ooze Rain）期间客户端每 tick 生成的雨滴粒子数量（原模组默认为 400，整合包可按需调低）")
                .defineInRange("oozeRainParticlesPerTick", 400, 0, 400);

        builder.pop();

        COMMON_SPEC = builder.build();
    }

    private EfBugfixConfig() {
    }
}
