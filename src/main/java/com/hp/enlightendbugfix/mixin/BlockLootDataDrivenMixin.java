package com.hp.enlightendbugfix.mixin;

import java.util.List;
import net.mcreator.enlightened_end.block.AncientStalk1Block;
import net.mcreator.enlightened_end.block.AncientStalk2Block;
import net.mcreator.enlightened_end.block.AncientStalk3Block;
import net.mcreator.enlightened_end.block.AquaticBismuthGlassPaneBlock;
import net.mcreator.enlightened_end.block.AquaticBismuthLampBlock;
import net.mcreator.enlightened_end.block.BismuthBlockBlock;
import net.mcreator.enlightened_end.block.BismuthGlassBlock;
import net.mcreator.enlightened_end.block.BismuthGlassPaneBlock;
import net.mcreator.enlightened_end.block.BismuthLampBlock;
import net.mcreator.enlightened_end.block.BismuthSheetSlabBlock;
import net.mcreator.enlightened_end.block.BismuthSheetStairsBlock;
import net.mcreator.enlightened_end.block.BismuthWallBlock;
import net.mcreator.enlightened_end.block.BlockofBismuthBlock;
import net.mcreator.enlightened_end.block.BlockofmalachiteBlock;
import net.mcreator.enlightened_end.block.BubbleJellyBlock;
import net.mcreator.enlightened_end.block.CeruleanUnderlilyBlock;
import net.mcreator.enlightened_end.block.CeruleanVineBlock;
import net.mcreator.enlightened_end.block.CeruleanVineTipBlock;
import net.mcreator.enlightened_end.block.ChiseledMalachiteBlock;
import net.mcreator.enlightened_end.block.ChiseledNullstoneBlock;
import net.mcreator.enlightened_end.block.ChiseledVoidShaleBlock;
import net.mcreator.enlightened_end.block.ChorloamBlock;
import net.mcreator.enlightened_end.block.ClosedGlowGourdBlock;
import net.mcreator.enlightened_end.block.ClosedMalachiteCapsuleBlock;
import net.mcreator.enlightened_end.block.CongealedFernBlock;
import net.mcreator.enlightened_end.block.CrackedEndStoneBricksBlock;
import net.mcreator.enlightened_end.block.CrackedEndStoneTilesBlock;
import net.mcreator.enlightened_end.block.CrackedMalachiteBricksBlock;
import net.mcreator.enlightened_end.block.CrackedMalachiteTilesBlock;
import net.mcreator.enlightened_end.block.CrackedNullstoneBricksBlock;
import net.mcreator.enlightened_end.block.CrackedPalerockTilesBlock;
import net.mcreator.enlightened_end.block.CrackedPurpurBlock;
import net.mcreator.enlightened_end.block.CrackedVoidShaleBricksBlock;
import net.mcreator.enlightened_end.block.CrackedVoidShaleTilesBlock;
import net.mcreator.enlightened_end.block.DazzlingBismuthBlockBlock;
import net.mcreator.enlightened_end.block.DazzlingBismuthGlassBlock;
import net.mcreator.enlightened_end.block.DazzlingBismuthLampBlock;
import net.mcreator.enlightened_end.block.DazzlingBismuthSheetsBlock;
import net.mcreator.enlightened_end.block.DazzlingBismuthSlabBlock;
import net.mcreator.enlightened_end.block.DazzlingBismuthWallBlock;
import net.mcreator.enlightened_end.block.DepletedSacrificalStatueBlock;
import net.mcreator.enlightened_end.block.DepletedUraniumBlockBlock;
import net.mcreator.enlightened_end.block.DepletedUraniumBricksBlock;
import net.mcreator.enlightened_end.block.DoubleCeruleanGrassTopBlock;
import net.mcreator.enlightened_end.block.EbonyButtonBlock;
import net.mcreator.enlightened_end.block.EbonyDoorBlock;
import net.mcreator.enlightened_end.block.EbonyFenceBlock;
import net.mcreator.enlightened_end.block.EbonyFenceGateBlock;
import net.mcreator.enlightened_end.block.EbonyPlanksBlock;
import net.mcreator.enlightened_end.block.EbonyPressurePlateBlock;
import net.mcreator.enlightened_end.block.EbonySlabBlock;
import net.mcreator.enlightened_end.block.EbonyStairsBlock;
import net.mcreator.enlightened_end.block.EbonyStalkBundleBlock;
import net.mcreator.enlightened_end.block.EbonyTrapdoorBlock;
import net.mcreator.enlightened_end.block.Elevibloom1Block;
import net.mcreator.enlightened_end.block.Elevibloom2Block;
import net.mcreator.enlightened_end.block.Elevibloom3Block;
import net.mcreator.enlightened_end.block.EndStoneSlabBlock;
import net.mcreator.enlightened_end.block.EndStoneStairsBlock;
import net.mcreator.enlightened_end.block.EndStoneTileSlabBlock;
import net.mcreator.enlightened_end.block.EndStoneTileStairsBlock;
import net.mcreator.enlightened_end.block.EndStoneTileWallBlock;
import net.mcreator.enlightened_end.block.EndStoneTilesBlock;
import net.mcreator.enlightened_end.block.EndStoneWallBlock;
import net.mcreator.enlightened_end.block.EnnegelSplatBlock;
import net.mcreator.enlightened_end.block.EtherealBismuthBlockBlock;
import net.mcreator.enlightened_end.block.EtherealBismuthGlassBlock;
import net.mcreator.enlightened_end.block.EtherealBismuthLampBlock;
import net.mcreator.enlightened_end.block.EtherealBismuthSheetsBlock;
import net.mcreator.enlightened_end.block.FreezingTotemBlock;
import net.mcreator.enlightened_end.block.FrostburntVoidshaleBlock;
import net.mcreator.enlightened_end.block.GasVentBlock;
import net.mcreator.enlightened_end.block.GasVentBottomBlock;
import net.mcreator.enlightened_end.block.GasVentHeliumBlock;
import net.mcreator.enlightened_end.block.GasVentLavaBlock;
import net.mcreator.enlightened_end.block.GasVentMiddleBlock;
import net.mcreator.enlightened_end.block.GasVentOozeBlock;
import net.mcreator.enlightened_end.block.GasVentWaterBlock;
import net.mcreator.enlightened_end.block.GasVentXenonBlock;
import net.mcreator.enlightened_end.block.GlaciumBlockBlock;
import net.mcreator.enlightened_end.block.GlaciumBrickSlabBlock;
import net.mcreator.enlightened_end.block.GlaciumBrickStairsBlock;
import net.mcreator.enlightened_end.block.GlaciumBrickWallBlock;
import net.mcreator.enlightened_end.block.GlaciumBricksBlock;
import net.mcreator.enlightened_end.block.GlowseepBlock;
import net.mcreator.enlightened_end.block.HangingEnnegelBlock;
import net.mcreator.enlightened_end.block.HeliosandBlock;
import net.mcreator.enlightened_end.block.HeliumLanternBlock;
import net.mcreator.enlightened_end.block.IndigoRootsBlock;
import net.mcreator.enlightened_end.block.IridescentBismuthBlockBlock;
import net.mcreator.enlightened_end.block.IridescentBismuthGlassBlock;
import net.mcreator.enlightened_end.block.IridescentBismuthLampBlock;
import net.mcreator.enlightened_end.block.IrradiumBlockBlock;
import net.mcreator.enlightened_end.block.IrradiumBrickSlabBlock;
import net.mcreator.enlightened_end.block.IrradiumBrickStairsBlock;
import net.mcreator.enlightened_end.block.IrradiumBrickWallBlock;
import net.mcreator.enlightened_end.block.LaserBlock;
import net.mcreator.enlightened_end.block.LaserOnBlock;
import net.mcreator.enlightened_end.block.MalachiteBrickSlabBlock;
import net.mcreator.enlightened_end.block.MalachiteBrickStairsBlock;
import net.mcreator.enlightened_end.block.MalachiteBrickWallBlock;
import net.mcreator.enlightened_end.block.MalachiteBricksBlock;
import net.mcreator.enlightened_end.block.MalachiteCapsuleBlock;
import net.mcreator.enlightened_end.block.MalachitePanelBlock;
import net.mcreator.enlightened_end.block.MalachiteReflectorDownBlock;
import net.mcreator.enlightened_end.block.MalachiteSlabBlock;
import net.mcreator.enlightened_end.block.MalachiteStairsBlock;
import net.mcreator.enlightened_end.block.MalachiteTIlesBlock;
import net.mcreator.enlightened_end.block.MalachiteTileSlabBlock;
import net.mcreator.enlightened_end.block.MalachiteTileStairsBlock;
import net.mcreator.enlightened_end.block.MalachiteTileWallBlock;
import net.mcreator.enlightened_end.block.MalachiteWallBlock;
import net.mcreator.enlightened_end.block.NuclearBombBlock;
import net.mcreator.enlightened_end.block.NuclearFurnaceBlock;
import net.mcreator.enlightened_end.block.NullstoneBlock;
import net.mcreator.enlightened_end.block.NullstoneBrickSlabBlock;
import net.mcreator.enlightened_end.block.NullstoneBrickStairsBlock;
import net.mcreator.enlightened_end.block.NullstoneBrickWallBlock;
import net.mcreator.enlightened_end.block.NullstoneBricksBlock;
import net.mcreator.enlightened_end.block.NullstoneButtonBlock;
import net.mcreator.enlightened_end.block.NullstonePressurePlateBlock;
import net.mcreator.enlightened_end.block.NullstoneSlabBlock;
import net.mcreator.enlightened_end.block.NullstoneStairsBlock;
import net.mcreator.enlightened_end.block.NullstoneWallBlock;
import net.mcreator.enlightened_end.block.OddRootsBlock;
import net.mcreator.enlightened_end.block.OozeCauldron1Block;
import net.mcreator.enlightened_end.block.OozeCauldron2Block;
import net.mcreator.enlightened_end.block.OozeCauldronFullBlock;
import net.mcreator.enlightened_end.block.OozeTubesBlock;
import net.mcreator.enlightened_end.block.OpenOddRootsBlock;
import net.mcreator.enlightened_end.block.PaleRockTileSlabBlock;
import net.mcreator.enlightened_end.block.PalerockTileStairsBlock;
import net.mcreator.enlightened_end.block.PalerockTileWallBlock;
import net.mcreator.enlightened_end.block.PalerockTilesBlock;
import net.mcreator.enlightened_end.block.PlanetaryAirBlock;
import net.mcreator.enlightened_end.block.PlanterySystemAirBlock;
import net.mcreator.enlightened_end.block.PoiseBismuthBlockBlock;
import net.mcreator.enlightened_end.block.PoiseBismuthGlassBlock;
import net.mcreator.enlightened_end.block.PoiseBismuthSheetSlabBlock;
import net.mcreator.enlightened_end.block.PoiseBismuthSheetWallBlock;
import net.mcreator.enlightened_end.block.PoiseBismuthSheetsBlock;
import net.mcreator.enlightened_end.block.PolishedMalachiteBlock;
import net.mcreator.enlightened_end.block.PolishedMalachiteSlabBlock;
import net.mcreator.enlightened_end.block.PolishedMalachiteWallBlock;
import net.mcreator.enlightened_end.block.PolishedNullstoneBlock;
import net.mcreator.enlightened_end.block.PolishedNullstoneSlabBlock;
import net.mcreator.enlightened_end.block.PolishedNullstoneStairsBlock;
import net.mcreator.enlightened_end.block.PolishedNullstoneWallBlock;
import net.mcreator.enlightened_end.block.PolishedVoidShaleBlock;
import net.mcreator.enlightened_end.block.PolishedVoidShaleSlabBlock;
import net.mcreator.enlightened_end.block.PolishedVoidShaleWallBlock;
import net.mcreator.enlightened_end.block.PureRadiumBlockBlock;
import net.mcreator.enlightened_end.block.PurpurWallBlock;
import net.mcreator.enlightened_end.block.RawBismuthBlockBlock;
import net.mcreator.enlightened_end.block.RawUraniumBlockBlock;
import net.mcreator.enlightened_end.block.SacrificalStatueBlock;
import net.mcreator.enlightened_end.block.SeldgeButtonBlock;
import net.mcreator.enlightened_end.block.SeldgeDoorBlock;
import net.mcreator.enlightened_end.block.SeldgeFenceBlock;
import net.mcreator.enlightened_end.block.SeldgeFenceGateBlock;
import net.mcreator.enlightened_end.block.SeldgeHyphaeBlock;
import net.mcreator.enlightened_end.block.SeldgePlanksBlock;
import net.mcreator.enlightened_end.block.SeldgePressurePlateBlock;
import net.mcreator.enlightened_end.block.SeldgeSlabBlock;
import net.mcreator.enlightened_end.block.SeldgeStairsBlock;
import net.mcreator.enlightened_end.block.SeldgeStemBlock;
import net.mcreator.enlightened_end.block.SeldgeTrapdoorBlock;
import net.mcreator.enlightened_end.block.SereneBismuthBlockBlock;
import net.mcreator.enlightened_end.block.SereneBismuthGlassBlock;
import net.mcreator.enlightened_end.block.SereneBismuthGlassPaneBlock;
import net.mcreator.enlightened_end.block.SereneBismuthLampBlock;
import net.mcreator.enlightened_end.block.SereneBismuthSheetSlabBlock;
import net.mcreator.enlightened_end.block.SereneBismuthSheetWallBlock;
import net.mcreator.enlightened_end.block.SereneBismuthSheetsBlock;
import net.mcreator.enlightened_end.block.SmoothPurpurBlock;
import net.mcreator.enlightened_end.block.SmoothPurpurSlabBlock;
import net.mcreator.enlightened_end.block.SmoothPurpurStairsBlock;
import net.mcreator.enlightened_end.block.SmoothPurpurWallBlock;
import net.mcreator.enlightened_end.block.SpringLeafDeactivatedBlock;
import net.mcreator.enlightened_end.block.SpringleafBlock;
import net.mcreator.enlightened_end.block.SpringleafVineBlock;
import net.mcreator.enlightened_end.block.StrippedCeruleanStalkBlockBlock;
import net.mcreator.enlightened_end.block.StrippedSeldgeHyphaeBlock;
import net.mcreator.enlightened_end.block.StrippedSeldgeLogBlock;
import net.mcreator.enlightened_end.block.TeleporterBlock;
import net.mcreator.enlightened_end.block.UraniumLampBlock;
import net.mcreator.enlightened_end.block.UraniumLampOffBlock;
import net.mcreator.enlightened_end.block.VerdantBismuthBlockBlock;
import net.mcreator.enlightened_end.block.VerdantBismuthGlassBlock;
import net.mcreator.enlightened_end.block.VerdantBismuthLampBlock;
import net.mcreator.enlightened_end.block.VerdantBismuthSheetsBlock;
import net.mcreator.enlightened_end.block.VoidShaleBlock;
import net.mcreator.enlightened_end.block.VoidShaleBrickSlabBlock;
import net.mcreator.enlightened_end.block.VoidShaleBrickStairsBlock;
import net.mcreator.enlightened_end.block.VoidShaleBrickWallBlock;
import net.mcreator.enlightened_end.block.VoidShaleBricksBlock;
import net.mcreator.enlightened_end.block.VoidShaleButtonBlock;
import net.mcreator.enlightened_end.block.VoidShalePressurePlateBlock;
import net.mcreator.enlightened_end.block.VoidShaleSlabBlock;
import net.mcreator.enlightened_end.block.VoidShaleStairsBlock;
import net.mcreator.enlightened_end.block.VoidShaleTileSlabBlock;
import net.mcreator.enlightened_end.block.VoidShaleTileWallBlock;
import net.mcreator.enlightened_end.block.VoidShaleTilesBlock;
import net.mcreator.enlightened_end.block.VoidShaleWallBlock;
import net.mcreator.enlightened_end.block.VoidshaleTileStairsBlock;
import net.mcreator.enlightened_end.block.VoidsteelBlockBlock;
import net.mcreator.enlightened_end.block.WaxedBismuthBlock;
import net.mcreator.enlightened_end.block.WaxedBismuthBlockBlock;
import net.mcreator.enlightened_end.block.WaxedBismuthGlassBlock;
import net.mcreator.enlightened_end.block.WaxedBismuthGlassPaneBlock;
import net.mcreator.enlightened_end.block.WaxedBismuthLampBlock;
import net.mcreator.enlightened_end.block.WaxedBismuthSheetWallBlock;
import net.mcreator.enlightened_end.block.WaxedBismuthSlabBlock;
import net.mcreator.enlightened_end.block.WaxedBismuthStairsBlock;
import net.mcreator.enlightened_end.block.XenonLanternBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 将全部 215 个原本依靠硬编码 getDrops() 掉落的终末之光方块改为纯数据驱动战利品表（委派回 super.getDrops），
 * 使 JEI/JER/EMI Loot 能完整展示方块掉落，并允许数据包与 LootJS 自由覆盖。
 */
@Mixin({
        AncientStalk1Block.class,
        AncientStalk2Block.class,
        AncientStalk3Block.class,
        AquaticBismuthGlassPaneBlock.class,
        AquaticBismuthLampBlock.class,
        BismuthBlockBlock.class,
        BismuthGlassBlock.class,
        BismuthGlassPaneBlock.class,
        BismuthLampBlock.class,
        BismuthSheetSlabBlock.class,
        BismuthSheetStairsBlock.class,
        BismuthWallBlock.class,
        BlockofBismuthBlock.class,
        BlockofmalachiteBlock.class,
        BubbleJellyBlock.class,
        CeruleanUnderlilyBlock.class,
        CeruleanVineBlock.class,
        CeruleanVineTipBlock.class,
        ChiseledMalachiteBlock.class,
        ChiseledNullstoneBlock.class,
        ChiseledVoidShaleBlock.class,
        ChorloamBlock.class,
        ClosedGlowGourdBlock.class,
        ClosedMalachiteCapsuleBlock.class,
        CongealedFernBlock.class,
        CrackedEndStoneBricksBlock.class,
        CrackedEndStoneTilesBlock.class,
        CrackedMalachiteBricksBlock.class,
        CrackedMalachiteTilesBlock.class,
        CrackedNullstoneBricksBlock.class,
        CrackedPalerockTilesBlock.class,
        CrackedPurpurBlock.class,
        CrackedVoidShaleBricksBlock.class,
        CrackedVoidShaleTilesBlock.class,
        DazzlingBismuthBlockBlock.class,
        DazzlingBismuthGlassBlock.class,
        DazzlingBismuthLampBlock.class,
        DazzlingBismuthSheetsBlock.class,
        DazzlingBismuthSlabBlock.class,
        DazzlingBismuthWallBlock.class,
        DepletedSacrificalStatueBlock.class,
        DepletedUraniumBlockBlock.class,
        DepletedUraniumBricksBlock.class,
        DoubleCeruleanGrassTopBlock.class,
        EbonyButtonBlock.class,
        EbonyDoorBlock.class,
        EbonyFenceBlock.class,
        EbonyFenceGateBlock.class,
        EbonyPlanksBlock.class,
        EbonyPressurePlateBlock.class,
        EbonySlabBlock.class,
        EbonyStairsBlock.class,
        EbonyStalkBundleBlock.class,
        EbonyTrapdoorBlock.class,
        Elevibloom1Block.class,
        Elevibloom2Block.class,
        Elevibloom3Block.class,
        EndStoneSlabBlock.class,
        EndStoneStairsBlock.class,
        EndStoneTileSlabBlock.class,
        EndStoneTileStairsBlock.class,
        EndStoneTileWallBlock.class,
        EndStoneTilesBlock.class,
        EndStoneWallBlock.class,
        EnnegelSplatBlock.class,
        EtherealBismuthBlockBlock.class,
        EtherealBismuthGlassBlock.class,
        EtherealBismuthLampBlock.class,
        EtherealBismuthSheetsBlock.class,
        FreezingTotemBlock.class,
        FrostburntVoidshaleBlock.class,
        GasVentBlock.class,
        GasVentBottomBlock.class,
        GasVentHeliumBlock.class,
        GasVentLavaBlock.class,
        GasVentMiddleBlock.class,
        GasVentOozeBlock.class,
        GasVentWaterBlock.class,
        GasVentXenonBlock.class,
        GlaciumBlockBlock.class,
        GlaciumBrickSlabBlock.class,
        GlaciumBrickStairsBlock.class,
        GlaciumBrickWallBlock.class,
        GlaciumBricksBlock.class,
        GlowseepBlock.class,
        HangingEnnegelBlock.class,
        HeliosandBlock.class,
        HeliumLanternBlock.class,
        IndigoRootsBlock.class,
        IridescentBismuthBlockBlock.class,
        IridescentBismuthGlassBlock.class,
        IridescentBismuthLampBlock.class,
        IrradiumBlockBlock.class,
        IrradiumBrickSlabBlock.class,
        IrradiumBrickStairsBlock.class,
        IrradiumBrickWallBlock.class,
        LaserBlock.class,
        LaserOnBlock.class,
        MalachiteBrickSlabBlock.class,
        MalachiteBrickStairsBlock.class,
        MalachiteBrickWallBlock.class,
        MalachiteBricksBlock.class,
        MalachiteCapsuleBlock.class,
        MalachitePanelBlock.class,
        MalachiteReflectorDownBlock.class,
        MalachiteSlabBlock.class,
        MalachiteStairsBlock.class,
        MalachiteTIlesBlock.class,
        MalachiteTileSlabBlock.class,
        MalachiteTileStairsBlock.class,
        MalachiteTileWallBlock.class,
        MalachiteWallBlock.class,
        NuclearBombBlock.class,
        NuclearFurnaceBlock.class,
        NullstoneBlock.class,
        NullstoneBrickSlabBlock.class,
        NullstoneBrickStairsBlock.class,
        NullstoneBrickWallBlock.class,
        NullstoneBricksBlock.class,
        NullstoneButtonBlock.class,
        NullstonePressurePlateBlock.class,
        NullstoneSlabBlock.class,
        NullstoneStairsBlock.class,
        NullstoneWallBlock.class,
        OddRootsBlock.class,
        OozeCauldron1Block.class,
        OozeCauldron2Block.class,
        OozeCauldronFullBlock.class,
        OozeTubesBlock.class,
        OpenOddRootsBlock.class,
        PaleRockTileSlabBlock.class,
        PalerockTileStairsBlock.class,
        PalerockTileWallBlock.class,
        PalerockTilesBlock.class,
        PlanetaryAirBlock.class,
        PlanterySystemAirBlock.class,
        PoiseBismuthBlockBlock.class,
        PoiseBismuthGlassBlock.class,
        PoiseBismuthSheetSlabBlock.class,
        PoiseBismuthSheetWallBlock.class,
        PoiseBismuthSheetsBlock.class,
        PolishedMalachiteBlock.class,
        PolishedMalachiteSlabBlock.class,
        PolishedMalachiteWallBlock.class,
        PolishedNullstoneBlock.class,
        PolishedNullstoneSlabBlock.class,
        PolishedNullstoneStairsBlock.class,
        PolishedNullstoneWallBlock.class,
        PolishedVoidShaleBlock.class,
        PolishedVoidShaleSlabBlock.class,
        PolishedVoidShaleWallBlock.class,
        PureRadiumBlockBlock.class,
        PurpurWallBlock.class,
        RawBismuthBlockBlock.class,
        RawUraniumBlockBlock.class,
        SacrificalStatueBlock.class,
        SeldgeButtonBlock.class,
        SeldgeDoorBlock.class,
        SeldgeFenceBlock.class,
        SeldgeFenceGateBlock.class,
        SeldgeHyphaeBlock.class,
        SeldgePlanksBlock.class,
        SeldgePressurePlateBlock.class,
        SeldgeSlabBlock.class,
        SeldgeStairsBlock.class,
        SeldgeStemBlock.class,
        SeldgeTrapdoorBlock.class,
        SereneBismuthBlockBlock.class,
        SereneBismuthGlassBlock.class,
        SereneBismuthGlassPaneBlock.class,
        SereneBismuthLampBlock.class,
        SereneBismuthSheetSlabBlock.class,
        SereneBismuthSheetWallBlock.class,
        SereneBismuthSheetsBlock.class,
        SmoothPurpurBlock.class,
        SmoothPurpurSlabBlock.class,
        SmoothPurpurStairsBlock.class,
        SmoothPurpurWallBlock.class,
        SpringLeafDeactivatedBlock.class,
        SpringleafBlock.class,
        SpringleafVineBlock.class,
        StrippedCeruleanStalkBlockBlock.class,
        StrippedSeldgeHyphaeBlock.class,
        StrippedSeldgeLogBlock.class,
        TeleporterBlock.class,
        UraniumLampBlock.class,
        UraniumLampOffBlock.class,
        VerdantBismuthBlockBlock.class,
        VerdantBismuthGlassBlock.class,
        VerdantBismuthLampBlock.class,
        VerdantBismuthSheetsBlock.class,
        VoidShaleBlock.class,
        VoidShaleBrickSlabBlock.class,
        VoidShaleBrickStairsBlock.class,
        VoidShaleBrickWallBlock.class,
        VoidShaleBricksBlock.class,
        VoidShaleButtonBlock.class,
        VoidShalePressurePlateBlock.class,
        VoidShaleSlabBlock.class,
        VoidShaleStairsBlock.class,
        VoidShaleTileSlabBlock.class,
        VoidShaleTileWallBlock.class,
        VoidShaleTilesBlock.class,
        VoidShaleWallBlock.class,
        VoidshaleTileStairsBlock.class,
        VoidsteelBlockBlock.class,
        WaxedBismuthBlock.class,
        WaxedBismuthBlockBlock.class,
        WaxedBismuthGlassBlock.class,
        WaxedBismuthGlassPaneBlock.class,
        WaxedBismuthLampBlock.class,
        WaxedBismuthSheetWallBlock.class,
        WaxedBismuthSlabBlock.class,
        WaxedBismuthStairsBlock.class,
        XenonLanternBlock.class
})
public abstract class BlockLootDataDrivenMixin extends BlockBehaviour {
    protected BlockLootDataDrivenMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getDrops", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$useDataDrivenLootTable(
            BlockState state,
            LootParams.Builder builder,
            CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        cir.setReturnValue(super.getDrops(state, builder));
    }
}
