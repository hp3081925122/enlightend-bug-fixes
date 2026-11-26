package com.hp.enlightendbugfix.util;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

/**
 * 为原模组缺失地图色的 119 个可见方块提供按贴图主色调匹配的 MapColor 映射表。
 * 空气占位方块、氦气/氙气方块与激光束保持 MapColor.NONE 不变。
 */
public final class EfMapColorRegistry {
    private static final Map<Class<?>, MapColor> COLOR_BY_CLASS = new IdentityHashMap<>(160);

    static {
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.AncientStalk1Block.class, MapColor.TERRACOTTA_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.AncientStalk2Block.class, MapColor.WARPED_NYLIUM);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.AncientStalk3Block.class, MapColor.WARPED_NYLIUM);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.AquaticBismuthGlassPaneBlock.class, MapColor.ICE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.AquaticBismuthLampBlock.class, MapColor.ICE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.AzureBerryVine0Block.class, MapColor.COLOR_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.AzureBerryVine1Block.class, MapColor.COLOR_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.AzureBerryVine2Block.class, MapColor.COLOR_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.BismuthGlassBlock.class, MapColor.TERRACOTTA_WHITE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.BismuthGlassPaneBlock.class, MapColor.WOOL);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.BismuthLampBlock.class, MapColor.WOOL);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.BounclingNut0Block.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.BounclingNut2Block.class, MapColor.WARPED_HYPHAE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.BubbleJellyBlock.class, MapColor.COLOR_PINK);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.CeruleanFrondBlock.class, MapColor.WARPED_STEM);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.CeruleanStalk0Block.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.ChorloamBlock.class, MapColor.COLOR_PURPLE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.ClosedMalachiteCapsuleBlock.class, MapColor.WARPED_STEM);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.CrackedPurpurBlock.class, MapColor.COLOR_LIGHT_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DazzlingBismuthGlassBlock.class, MapColor.GOLD);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DazzlingBismuthGlassPaneBlock.class, MapColor.GOLD);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DazzlingBismuthLampBlock.class, MapColor.GOLD);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DepletedIrradiumBrickWallBlock.class, MapColor.DEEPSLATE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DepletedSacrificalStatueBlock.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DepletedUraniumBlockBlock.class, MapColor.DEEPSLATE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DepletedUraniumBricksBlock.class, MapColor.DEEPSLATE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DepletedUraniumBrickSlabBlock.class, MapColor.DEEPSLATE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DepletedUraniumBrickStairsBlock.class, MapColor.DEEPSLATE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DoubleCeruleanGrassBottomBlock.class, MapColor.COLOR_LIGHT_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.DoubleCeruleanGrassTopBlock.class, MapColor.WARPED_STEM);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EbonyDoorBlock.class, MapColor.COLOR_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EbonyFenceBlock.class, MapColor.COLOR_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EbonyFenceGateBlock.class, MapColor.COLOR_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EbonyPlanksBlock.class, MapColor.COLOR_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EbonySlabBlock.class, MapColor.COLOR_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EbonyStairsBlock.class, MapColor.COLOR_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EbonyStalkBundleBlock.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EndBismuthOreBlock.class, MapColor.TERRACOTTA_WHITE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EnnegelBlockBlock.class, MapColor.COLOR_PINK);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EnnegelSplatBlock.class, MapColor.COLOR_PINK);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EtherealBismuthGlassBlock.class, MapColor.ICE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EtherealBismuthGlassPaneBlock.class, MapColor.ICE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.EtherealBismuthLampBlock.class, MapColor.ICE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.FrostburnIceBlock.class, MapColor.LAPIS);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.FrostburntVoidshaleBlock.class, MapColor.COLOR_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GasVentBlock.class, MapColor.TERRACOTTA_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GasVentBottomBlock.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GasVentHeliumBlock.class, MapColor.COLOR_BROWN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GasVentLavaBlock.class, MapColor.TERRACOTTA_BROWN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GasVentMiddleBlock.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GasVentOozeBlock.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GasVentWaterBlock.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GasVentXenonBlock.class, MapColor.TERRACOTTA_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GlaciumBlockBlock.class, MapColor.COLOR_LIGHT_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GlaciumBricksBlock.class, MapColor.COLOR_LIGHT_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GlaciumBrickSlabBlock.class, MapColor.COLOR_LIGHT_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GlaciumBrickStairsBlock.class, MapColor.COLOR_LIGHT_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.GlaciumBrickWallBlock.class, MapColor.COLOR_LIGHT_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.HangingEnnegelBlock.class, MapColor.COLOR_PINK);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.HeliosandBlock.class, MapColor.COLOR_ORANGE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.HeliumLanternBlock.class, MapColor.WOOD);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.IridescentBismuthGlassBlock.class, MapColor.CLAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.IridescentBismuthGlassPaneBlock.class, MapColor.CLAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.IridescentBismuthLampBlock.class, MapColor.WOOL);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.IrradiumBlockBlock.class, MapColor.EMERALD);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.IrradiumBrickSlabBlock.class, MapColor.EMERALD);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.IrradiumBrickStairsBlock.class, MapColor.EMERALD);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.IrradiumBrickWallBlock.class, MapColor.EMERALD);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.LaserBlock.class, MapColor.TERRACOTTA_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.LaserOnBlock.class, MapColor.TERRACOTTA_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.MalachiteCapsuleBlock.class, MapColor.WARPED_STEM);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.MalachitePanelBlock.class, MapColor.TERRACOTTA_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.MalachitePanelHorizontalBlock.class, MapColor.TERRACOTTA_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.MalachiteReflectorDownBlock.class, MapColor.TERRACOTTA_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.MalachiteReflectorDownOnBlock.class, MapColor.TERRACOTTA_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.MalachiteReflectorHorizontalOnBlock.class, MapColor.TERRACOTTA_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.MalachiteReflectorVeritcalOnBlock.class, MapColor.TERRACOTTA_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.NuclearBombBlock.class, MapColor.GRASS);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.NuclearFurnaceBlock.class, MapColor.TERRACOTTA_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.OozeCauldron1Block.class, MapColor.COLOR_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.OozeCauldron2Block.class, MapColor.COLOR_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.OozeCauldronFullBlock.class, MapColor.COLOR_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.OozeTubesBlock.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.PoiseBismuthGlassBlock.class, MapColor.COLOR_LIGHT_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.PottedIndigoRootsBlock.class, MapColor.COLOR_PURPLE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.PottedIndigoSproutsBlock.class, MapColor.COLOR_PURPLE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.PottedOddRootsBlock.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.PottedOpenOddRootsBlock.class, MapColor.COLOR_LIGHT_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.PottedSeldgeSproutsBlock.class, MapColor.TERRACOTTA_MAGENTA);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.PureRadiumBlockBlock.class, MapColor.EMERALD);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.PurpurWallBlock.class, MapColor.COLOR_LIGHT_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.RawBismuthBlockBlock.class, MapColor.TERRACOTTA_LIGHT_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.RawUraniumBlockBlock.class, MapColor.COLOR_GREEN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SacrificalStatueBlock.class, MapColor.TERRACOTTA_BLUE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SeldgeDoorBlock.class, MapColor.COLOR_PURPLE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SereneBismuthGlassBlock.class, MapColor.COLOR_PINK);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SereneBismuthGlassPaneBlock.class, MapColor.COLOR_PINK);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SereneBismuthLampBlock.class, MapColor.COLOR_PINK);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SmoothPurpurBlock.class, MapColor.COLOR_LIGHT_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SmoothPurpurSlabBlock.class, MapColor.COLOR_LIGHT_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SmoothPurpurStairsBlock.class, MapColor.COLOR_LIGHT_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SmoothPurpurWallBlock.class, MapColor.COLOR_LIGHT_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SpringleafBlock.class, MapColor.CRIMSON_STEM);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SpringLeafDeactivatedBlock.class, MapColor.CRIMSON_STEM);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SpringleafVineBlock.class, MapColor.CRIMSON_STEM);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.StardustBlockBlock.class, MapColor.COLOR_YELLOW);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.StrippedCeruleanStalkBlockBlock.class, MapColor.COLOR_CYAN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.SuperheatedPotBlock.class, MapColor.COLOR_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.TeleporterBlock.class, MapColor.COLOR_GRAY);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.UraniumLampBlock.class, MapColor.GLOW_LICHEN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.UraniumLampOffBlock.class, MapColor.GLOW_LICHEN);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.UraniumOreBlock.class, MapColor.TERRACOTTA_WHITE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.VerdantBismuthGlassBlock.class, MapColor.DIAMOND);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.VerdantBismuthGlassPaneBlock.class, MapColor.DIAMOND);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.VerdantBismuthLampBlock.class, MapColor.DIAMOND);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.WaxedBismuthGlassBlock.class, MapColor.TERRACOTTA_WHITE);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.WaxedBismuthGlassPaneBlock.class, MapColor.WOOL);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.WaxedBismuthLampBlock.class, MapColor.WOOL);
        COLOR_BY_CLASS.put(net.mcreator.enlightened_end.block.XenonLanternBlock.class, MapColor.COLOR_CYAN);
    }

    private EfMapColorRegistry() {
    }

    public static MapColor resolve(Block block, MapColor fallback) {
        if (fallback != null && fallback != MapColor.NONE) {
            return fallback;
        }
        MapColor mapped = COLOR_BY_CLASS.get(block.getClass());
        return mapped != null ? mapped : (fallback != null ? fallback : MapColor.NONE);
    }
}
