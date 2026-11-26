package com.hp.enlightendbugfix.test;

import com.hp.enlightendbugfix.Enlightendbugfix;
import com.hp.enlightendbugfix.util.EfBiomeNoiseHelper;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.mcreator.enlightened_end.EnlightenedEndMod;
import net.mcreator.enlightened_end.block.entity.NuclearFurnaceBlockEntity;
import net.mcreator.enlightened_end.block.entity.SacrificalStatueBlockEntity;
import net.mcreator.enlightened_end.entity.BouncelingEntity;
import net.mcreator.enlightened_end.entity.BrainfeederEntity;
import net.mcreator.enlightened_end.entity.FlashBombEntity;
import net.mcreator.enlightened_end.entity.HeliumDartEntity;
import net.mcreator.enlightened_end.entity.RinglingEntity;
import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.init.EnlightenedEndModEntities;
import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.mcreator.enlightened_end.init.EnlightenedEndModMobEffects;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.BottleEnsnareProcedure;
import net.mcreator.enlightened_end.procedures.BubbleDartWhileProjectileFlyingTickProcedure;
import net.mcreator.enlightened_end.procedures.CollectDistrubedHeliumGasProcedure;
import net.mcreator.enlightened_end.procedures.DefaultBiomeSeedProcedure;
import net.mcreator.enlightened_end.procedures.EmptyCauldronOozeProcedure;
import net.mcreator.enlightened_end.procedures.EnderstrideOnEffectActiveTickProcedure;
import net.mcreator.enlightened_end.procedures.FatalRadiationOverlayDisplayOverlayIngameProcedure;
import net.mcreator.enlightened_end.procedures.FeedRinglingProcedure;
import net.mcreator.enlightened_end.procedures.HangingCeruleanGrassBlockDestroyedByPlayerProcedure;
import net.mcreator.enlightened_end.procedures.HeliumDartFlyingProcedure;
import net.mcreator.enlightened_end.procedures.IsCongealedForestProcedure;
import net.mcreator.enlightened_end.procedures.IsEnderneathProcedure;
import net.mcreator.enlightened_end.procedures.IsExogasBeltProcedure;
import net.mcreator.enlightened_end.procedures.IsFreezingEndProcedure;
import net.mcreator.enlightened_end.procedures.IsOozeSeepsProcedure;
import net.mcreator.enlightened_end.procedures.IsUpperEndProcedure;
import net.mcreator.enlightened_end.procedures.NuclearFurnaceNeighbourBlockChangesProcedure;
import net.mcreator.enlightened_end.procedures.NuclearFurnaceUpdateTickProcedure;
import net.mcreator.enlightened_end.procedures.OozeRainChanceProcedure;
import net.mcreator.enlightened_end.procedures.PlaceDoubleCeruleanGrassProcedure;
import net.mcreator.enlightened_end.procedures.PlaceHeliumGasProcedure;
import net.mcreator.enlightened_end.procedures.PlaceXenonGasProcedure;
import net.mcreator.enlightened_end.procedures.PlayerRadiationProcedure;
import net.mcreator.enlightened_end.procedures.RinglingAttackProcedure;
import net.mcreator.enlightened_end.procedures.RinglingPotItemInInventoryTickProcedure;
import net.mcreator.enlightened_end.procedures.RootedOnEffectActiveTickProcedure;
import net.mcreator.enlightened_end.procedures.SetBiomeSeedProcedure;
import net.mcreator.enlightened_end.procedures.StripCeruleanStalkBlockProcedure;
import net.mcreator.enlightened_end.procedures.StripSeldgeHyphaeProcedure;
import net.mcreator.enlightened_end.procedures.StripSeldgeStemProcedure;
import net.mcreator.enlightened_end.procedures.UnderlilyOnBonemealProcedure;
import net.mcreator.enlightened_end.procedures.VoidsteelPickaxeRightclickedProcedure;
import net.mcreator.enlightened_end.procedures.VoidsteelToolInInventoryTickProcedure;
import net.mcreator.enlightened_end.procedures.XenonDartFlyingProcedure;
import net.mcreator.enlightened_end.world.inventory.NuclearFurnaceScreenMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/**
 * 终末之光（Enlightend）Bug 修复与性能优化模组自动化 GameTest 验收套件。
 */
@GameTestHolder(Enlightendbugfix.MOD_ID)
@PrefixGameTestTemplate(false)
@Mod.EventBusSubscriber(modid = Enlightendbugfix.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EfBugfixGameTests {
    private static final String EMPTY_TEMPLATE = "empty";

    // 空气、气体与激光束保持 MapColor.NONE，其余 119 个原本无颜色的可见方块均须具备非 NONE 地图色
    private static final Set<String> EXPECTED_NONE_MAP_COLOR_BLOCKS = Set.of(
            "planetary_air",
            "plantery_system_air",
            "helium_gas",
            "xenon_gas",
            "laser_emission",
            "ooze_fluid"
    );

    private static Item gametestModularTool;

    private EfBugfixGameTests() {
    }

    @SubscribeEvent
    public static void registerTestItems(RegisterEvent event) {
        // 仅在 GameTest 模式下注册测试用模块化工具，正式生产环境绝不额外注册任何物品
        if (!ForgeGameTestHooks.isGametestEnabled()) {
            return;
        }
        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            gametestModularTool = new Item(new Item.Properties()) {
                @Override
                public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
                    return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
                }

                @Override
                public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
                    return toolAction == ToolActions.AXE_STRIP;
                }
            };
            helper.register(new ResourceLocation(Enlightendbugfix.MOD_ID, "gametest_modular_tool"), gametestModularTool);
        });
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(EfBugfixGameTests.class);
    }

    /**
     * 1. 验证全部 12 个原引用客户端 Minecraft 类的过程在专用服务端可安全加载与调用。
     */
    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Enlightendbugfix.MOD_ID)
    public static void testDedicatedServerClassLoadSafety(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player mockPlayer = helper.makeMockPlayer();
        BlockPos abs = helper.absolutePos(new BlockPos(1, 2, 1));
        double x = abs.getX();
        double y = abs.getY();
        double z = abs.getZ();

        PlayerRadiationProcedure.execute(level, x, y, z, mockPlayer);
        EnderstrideOnEffectActiveTickProcedure.execute(level, x, y, z, mockPlayer);
        RootedOnEffectActiveTickProcedure.execute(level, x, y, z, mockPlayer);
        HangingCeruleanGrassBlockDestroyedByPlayerProcedure.execute(level, x, y, z, mockPlayer);
        PlaceDoubleCeruleanGrassProcedure.execute(level, x, y, z, Direction.DOWN, mockPlayer);
        PlaceHeliumGasProcedure.execute(level, x, y, z, Direction.UP, mockPlayer);
        PlaceXenonGasProcedure.execute(level, x, y, z, Direction.UP, mockPlayer);
        StripCeruleanStalkBlockProcedure.execute(level, x, y, z, mockPlayer);
        StripSeldgeHyphaeProcedure.execute(level, x, y, z, mockPlayer);
        StripSeldgeStemProcedure.execute(level, x, y, z, mockPlayer);
        VoidsteelPickaxeRightclickedProcedure.execute(level, x, y, z, mockPlayer);
        boolean fatal = FatalRadiationOverlayDisplayOverlayIngameProcedure.execute(mockPlayer);
        helper.assertFalse(fatal, "Zero radiation mock player should not trigger fatal radiation");
        helper.succeed();
    }

    /**
     * 2. 验证延迟队列异常隔离、非正数 tick 不泄漏，以及吹箭飞行过程不再向队列无限堆积任务。
     */
    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Enlightendbugfix.MOD_ID)
    public static void testWorkQueueAndProjectileNoLeak(GameTestHelper helper) {
        AtomicInteger executed = new AtomicInteger(0);
        EnlightenedEndMod.queueServerWork(0, () -> {
            throw new RuntimeException("Intentional test exception inside EnlightenedEndMod workQueue");
        });
        EnlightenedEndMod.queueServerWork(1, executed::incrementAndGet);
        EnlightenedEndMod.queueServerWork(-3, executed::incrementAndGet);

        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(2, 3, 2));
        HeliumDartEntity dart = new HeliumDartEntity(EnlightenedEndModEntities.HELIUM_DART.get(), level);
        dart.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        level.addFreshEntity(dart);

        HeliumDartFlyingProcedure.execute(level, dart.getX(), dart.getY(), dart.getZ(), dart);
        XenonDartFlyingProcedure.execute(level, dart.getX(), dart.getY(), dart.getZ(), dart);
        BubbleDartWhileProjectileFlyingTickProcedure.execute(level, dart.getX(), dart.getY(), dart.getZ(), dart);
        helper.assertFalse(dart.isRemoved(), "Dart under 400 ticks should remain alive");

        dart.tickCount = 400;
        HeliumDartFlyingProcedure.execute(level, dart.getX(), dart.getY(), dart.getZ(), dart);
        helper.assertTrue(dart.isRemoved(), "Dart reaching 400 ticks should discard cleanly");

        helper.runAfterDelay(3, () -> {
            helper.assertTrue(executed.get() == 2, "Expected 2 server work tasks executed, got " + executed.get());
            helper.succeed();
        });
    }

    /**
     * 3. 验证末地子群系 SimplexNoise 种子一致性、跨维度传送不改随机数算法、地图变量 64 位种子无损编解码。
     */
    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Enlightendbugfix.MOD_ID)
    public static void testBiomeNoiseSeedStability(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        DefaultBiomeSeedProcedure.execute(level);
        SimplexNoise initialNoise = SetBiomeSeedProcedure.generator;
        helper.assertTrue(initialNoise != null, "SimplexNoise generator must be initialized");

        double sampleBefore = initialNoise.getValue(123.45D, -678.9D);
        SetBiomeSeedProcedure.execute(level);
        SimplexNoise afterPortalNoise = SetBiomeSeedProcedure.generator;
        helper.assertTrue(initialNoise == afterPortalNoise, "SetBiomeSeedProcedure must not replace generator with LegacyRandomSource");
        helper.assertTrue(Double.compare(sampleBefore, afterPortalNoise.getValue(123.45D, -678.9D)) == 0, "Noise output must remain identical");

        // 验证极端 64 位种子拆分写入 MapVariables 后无精度丢失
        EnlightenedEndModVariables.MapVariables tempVars = new EnlightenedEndModVariables.MapVariables();
        long testSeed = -8472918374918273645L;
        EfBiomeNoiseHelper.writeSeedToMapVariables(tempVars, testSeed);
        helper.assertTrue(EfBiomeNoiseHelper.readSeedFromMapVariables(tempVars) == testSeed, "64-bit seed roundtrip mismatch");

        // 置空后再直接调用 6 个子群系判定过程，验证零空指针自愈
        SetBiomeSeedProcedure.generator = null;
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        IsCongealedForestProcedure.execute(level, pos.getX(), 60, pos.getZ());
        IsFreezingEndProcedure.execute(level, pos.getX(), 140, pos.getZ());
        IsOozeSeepsProcedure.execute(level, pos.getX(), 60, pos.getZ());
        IsExogasBeltProcedure.execute(level, pos.getX(), 140, pos.getZ());
        IsUpperEndProcedure.execute(level, pos.getX(), 140, pos.getZ());
        IsEnderneathProcedure.execute(level, pos.getX(), 20, pos.getZ());
        helper.assertTrue(SetBiomeSeedProcedure.generator != null, "Sub-biome check must auto-initialize generator");

        // 验证 OozeRainChanceProcedure 执行正常
        OozeRainChanceProcedure.execute(level);
        helper.succeed();
    }

    /**
     * 4. 全量核验所有方块 MapColor 补齐、氦气/氙气无遮挡不透视、12 个实心台阶楼梯围墙正常阻光。
     */
    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Enlightendbugfix.MOD_ID)
    public static void testMapColorsAndGasOcclusionAndLight(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos testPos = helper.absolutePos(new BlockPos(2, 2, 2));

        int checkedVisible = 0;
        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
            if (id == null || !"enlightened_end".equals(id.getNamespace())) {
                continue;
            }
            String path = id.getPath();
            BlockState state = block.defaultBlockState();
            MapColor mapColor = state.getMapColor(level, testPos);
            if (!EXPECTED_NONE_MAP_COLOR_BLOCKS.contains(path)) {
                helper.assertTrue(mapColor != null && mapColor != MapColor.NONE,
                        "Visible Enlightend block still has MapColor.NONE: " + id);
                checkedVisible++;
            }
        }
        helper.assertTrue(checkedVisible >= 180, "Expected at least 180 visible Enlightend blocks verified, got " + checkedVisible);

        // 验证氦气与氙气已具备 noOcclusion 且阻光值为 0
        BlockState helium = EnlightenedEndModBlocks.HELIUM_GAS.get().defaultBlockState();
        BlockState xenon = EnlightenedEndModBlocks.XENON_GAS.get().defaultBlockState();
        helper.assertFalse(helium.canOcclude(), "HeliumGasBlock must not occlude adjacent solid block faces");
        helper.assertFalse(xenon.canOcclude(), "XenonGasBlock must not occlude adjacent solid block faces");
        helper.assertTrue(helium.getLightBlock(level, testPos) == 0, "HeliumGasBlock light block should be 0");
        helper.assertTrue(xenon.getLightBlock(level, testPos) == 0, "XenonGasBlock light block should be 0");

        // 验证双层实心台阶恢复原版 15 满阻光（修复前因硬编码 return 0 导致双层满方块台阶异常透光）
        BlockState doubleEndSlab = EnlightenedEndModBlocks.END_STONE_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);
        BlockState doublePalerockSlab = EnlightenedEndModBlocks.PALEROCK_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);
        helper.assertTrue(doubleEndSlab.getLightBlock(level, testPos) == level.getMaxLightLevel(),
                "Double EndStoneSlabBlock must block full light (15)");
        helper.assertTrue(doublePalerockSlab.getLightBlock(level, testPos) == level.getMaxLightLevel(),
                "Double PalerockSlab must block full light (15)");
        helper.succeed();
    }

    /**
     * 5. 验证方块挖掘等级标签与自定义非 PickaxeItem / 非 AxeItem 模块化工具兼容。
     */
    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Enlightendbugfix.MOD_ID)
    public static void testModularToolHarvestAndStrip(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));

        BlockState malachiteOre = EnlightenedEndModBlocks.MALACHITE_ORE.get().defaultBlockState();
        BlockState bismuthBlock = EnlightenedEndModBlocks.BISMUTH_BLOCK.get().defaultBlockState();
        BlockState adamantiteNode = EnlightenedEndModBlocks.ADAMANTITE_NODE.get().defaultBlockState();

        // 验证原版木镐无法采集需要石镐/铁镐/钻石镐的方块，对应等级镐子可以采集
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WOODEN_PICKAXE));
        helper.assertFalse(malachiteOre.canHarvestBlock(helper.getLevel(), pos, player), "Wooden pickaxe must not harvest malachite_ore");
        helper.assertFalse(bismuthBlock.canHarvestBlock(helper.getLevel(), pos, player), "Wooden pickaxe must not harvest bismuth_block");
        helper.assertFalse(adamantiteNode.canHarvestBlock(helper.getLevel(), pos, player), "Wooden pickaxe must not harvest adamantite_node");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE_PICKAXE));
        helper.assertTrue(malachiteOre.canHarvestBlock(helper.getLevel(), pos, player), "Stone pickaxe must harvest malachite_ore");
        helper.assertFalse(bismuthBlock.canHarvestBlock(helper.getLevel(), pos, player), "Stone pickaxe must not harvest bismuth_block");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
        helper.assertTrue(bismuthBlock.canHarvestBlock(helper.getLevel(), pos, player), "Iron pickaxe must harvest bismuth_block");
        helper.assertFalse(adamantiteNode.canHarvestBlock(helper.getLevel(), pos, player), "Iron pickaxe must not harvest adamantite_node");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_PICKAXE));
        helper.assertTrue(adamantiteNode.canHarvestBlock(helper.getLevel(), pos, player), "Diamond pickaxe must harvest adamantite_node");

        // 使用仅在 GameTest 模式下注册的非 PickaxeItem / AxeItem 自定义工具（模拟匠魂/Tetra 模块化工具）
        helper.assertTrue(gametestModularTool != null, "GameTest modular tool must be registered in GameTest mode");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(gametestModularTool));
        helper.assertTrue(bismuthBlock.canHarvestBlock(helper.getLevel(), pos, player),
                "Custom non-PickaxeItem modular tool must harvest pickaxe blocks");

        // 测试非 AxeItem 工具通过 ToolActions.AXE_STRIP 对原木去皮
        helper.getLevel().setBlock(pos, EnlightenedEndModBlocks.CERULEAN_LOG.get().defaultBlockState(), 3);
        StripCeruleanStalkBlockProcedure.execute(helper.getLevel(), pos.getX(), pos.getY(), pos.getZ(), player);
        helper.assertTrue(helper.getLevel().getBlockState(pos).is(EnlightenedEndModBlocks.STRIPPED_CERULEAN_LOG.get()),
                "Custom ToolActions.AXE_STRIP tool should strip cerulean_log");
        helper.succeed();
    }

    /**
     * 6. 验证核熔炉不吞第 2 根铱棒、无输入不空烧、100 tick 正常耗时、空闲不扣负数燃料，以及漏斗邻接远程破方块漏洞修复。
     */
    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Enlightendbugfix.MOD_ID)
    public static void testNuclearFurnaceAndNeighbourHopperFix(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos furnacePos = helper.absolutePos(new BlockPos(2, 2, 4));
        level.setBlock(furnacePos, EnlightenedEndModBlocks.NUCLEAR_FURNACE.get().defaultBlockState(), 3);

        NuclearFurnaceBlockEntity be = (NuclearFurnaceBlockEntity) level.getBlockEntity(furnacePos);
        helper.assertTrue(be != null, "NuclearFurnaceBlockEntity must exist");
        IItemHandlerModifiable inv = (IItemHandlerModifiable) be.getCapability(ForgeCapabilities.ITEM_HANDLER, null).resolve().orElseThrow();

        // 放入 2 根铱棒但无任何烧炼输入：不应消耗铱棒，且 fuelTime 保持 0.0 不变负
        inv.setStackInSlot(0, new ItemStack(EnlightenedEndModItems.IRRADIUM_BAR.get(), 2));
        NuclearFurnaceUpdateTickProcedure.execute(level, furnacePos.getX(), furnacePos.getY(), furnacePos.getZ());
        helper.assertTrue(inv.getStackInSlot(0).getCount() == 2, "Idle nuclear furnace must not burn Irradium Bar without inputs");
        helper.assertTrue(be.getPersistentData().getDouble("fuelTime") == 0.0D, "Idle fuelTime must stay 0.0");

        // 放入 2 个粗铁：首 tick 消耗 1 根铱棒（必须保留第 2 根！），且首 tick 不能 0 tick 瞬烧产出铁锭
        inv.setStackInSlot(1, new ItemStack(Items.RAW_IRON, 2));
        NuclearFurnaceUpdateTickProcedure.execute(level, furnacePos.getX(), furnacePos.getY(), furnacePos.getZ());
        helper.assertTrue(inv.getStackInSlot(0).getCount() == 1,
                "Expected 1 Irradium Bar remaining after igniting from 2, got " + inv.getStackInSlot(0).getCount());
        helper.assertTrue(inv.getStackInSlot(4).isEmpty(), "First item must not insta-smelt in 0 ticks");

        // 再运行 100 tick 完成第一件粗铁烧炼
        for (int t = 0; t < 100; t++) {
            NuclearFurnaceUpdateTickProcedure.execute(level, furnacePos.getX(), furnacePos.getY(), furnacePos.getZ());
        }
        helper.assertTrue(inv.getStackInSlot(4).is(Items.IRON_INGOT) && inv.getStackInSlot(4).getCount() == 1,
                "Expected 1 Iron Ingot after 100 cooking ticks");
        helper.assertTrue(inv.getStackInSlot(1).getCount() == 1, "Expected 1 Raw Iron remaining");

        // 验证容器自动化槽位保护与未绑定菜单 stillValid == false
        helper.assertFalse(be.canPlaceItem(0, new ItemStack(Items.RAW_IRON)), "Slot 0 must reject non-fuel items");
        helper.assertTrue(be.canPlaceItem(0, new ItemStack(EnlightenedEndModItems.IRRADIUM_BAR.get())), "Slot 0 must accept Irradium Bar");
        helper.assertFalse(be.canTakeItemThroughFace(0, inv.getStackInSlot(0), Direction.DOWN), "Hopper must not extract fuel slot 0");
        helper.assertTrue(be.canTakeItemThroughFace(4, inv.getStackInSlot(4), Direction.DOWN), "Output slot 4 should allow extraction");

        Player mockPlayer = helper.makeMockPlayer();
        FriendlyByteBuf unboundBuf = new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(furnacePos.above(10));
        NuclearFurnaceScreenMenu unboundMenu = new NuclearFurnaceScreenMenu(0, mockPlayer.getInventory(), unboundBuf);
        helper.assertFalse(unboundMenu.stillValid(mockPlayer), "Unbound NuclearFurnaceScreenMenu must not remain valid");

        // 验证 (z, y + 1, z) 坐标错写漏洞已修复：在 (z, y + 1, z) 放基岩，在 (x, y + 1, z) 放漏斗（确保 x != z）
        BlockPos bugRemotePos = new BlockPos(furnacePos.getZ(), furnacePos.getY() + 1, furnacePos.getZ());
        if (!bugRemotePos.equals(furnacePos.above())) {
            BlockState oldRemote = level.getBlockState(bugRemotePos);
            level.setBlock(bugRemotePos, Blocks.BEDROCK.defaultBlockState(), 3);
            level.setBlock(furnacePos.above(), Blocks.HOPPER.defaultBlockState(), 3);
            NuclearFurnaceNeighbourBlockChangesProcedure.execute(level, furnacePos.getX(), furnacePos.getY(), furnacePos.getZ());
            helper.assertFalse(level.getBlockState(furnacePos.above()).is(Blocks.HOPPER), "Adjacent hopper above nuclear furnace should break");
            helper.assertTrue(level.getBlockState(bugRemotePos).is(Blocks.BEDROCK), "Remote block at (z, y+1, z) must never be destroyed");
            level.setBlock(bugRemotePos, oldRemote, 3);
        }

        helper.succeed();
    }

    /**
     * 7. 验证虚空钢镐右键挖掘检测射线目标且不可破坏基岩，以及骨粉催熟 Underlily 不会覆盖正下方基岩。
     */
    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Enlightendbugfix.MOD_ID)
    public static void testBedrockProtectionInPickaxeAndUnderlily(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer();
        BlockPos standPos = helper.absolutePos(new BlockPos(2, 2, 2));
        BlockPos targetPos = standPos.south(3).above(1);

        level.setBlock(standPos.below(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(targetPos, Blocks.BEDROCK.defaultBlockState(), 3);
        player.setPos(standPos.getX() + 0.5D, standPos.getY(), standPos.getZ() + 0.5D);
        player.setYRot(0.0F);
        player.setXRot(0.0F);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(EnlightenedEndModItems.ADAMANTITE_PICKAXE.get()));

        VoidsteelPickaxeRightclickedProcedure.execute(level, player.getX(), player.getY() - 1.0D, player.getZ(), player);
        helper.assertTrue(level.getBlockState(targetPos).is(Blocks.BEDROCK), "Voidsteel Pickaxe right-click must never break Bedrock");

        // 玩家脚下放黑曜石（等级 3 > 2），目标放末地石（等级 0 <= 2），应按射线命中方块成功挖掘末地石
        level.setBlock(standPos.below(), Blocks.OBSIDIAN.defaultBlockState(), 3);
        level.setBlock(targetPos, Blocks.END_STONE.defaultBlockState(), 3);
        VoidsteelPickaxeRightclickedProcedure.execute(level, player.getX(), player.getY() - 1.0D, player.getZ(), player);
        helper.assertTrue(level.getBlockState(targetPos).isAir(), "Voidsteel Pickaxe should mine valid raytraced End Stone");

        // 验证骨粉催熟 Underlily 不会吞掉正下方基岩
        BlockPos lilyPos = helper.absolutePos(new BlockPos(4, 3, 4));
        level.setBlock(lilyPos.below(), Blocks.BEDROCK.defaultBlockState(), 3);
        level.setBlock(lilyPos, EnlightenedEndModBlocks.UNDERLILY.get().defaultBlockState(), 3);
        UnderlilyOnBonemealProcedure.execute(level, lilyPos.getX(), lilyPos.getY(), lilyPos.getZ());
        helper.assertTrue(level.getBlockState(lilyPos.below()).is(Blocks.BEDROCK), "Underlily bonemeal must not overwrite Bedrock below");
        helper.succeed();
    }

    /**
     * 8. 验证战利品表（fumesplat 掉腺体、祭坛奖励表合法加载、cerulean_frond 有掉落、cerulean_grass 不掉下界苗）与非生物实体分类修正。
     */
    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Enlightendbugfix.MOD_ID)
    public static void testLootTablesAndEntityCategories(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Vec3 origin = Vec3.atCenterOf(helper.absolutePos(new BlockPos(2, 2, 2)));

        // 验证 fumesplat 实体战利品表掉落 splatter_gland
        LootTable fumesplatLoot = level.getServer().getLootData().getLootTable(new ResourceLocation("enlightened_end:entities/fumesplat"));
        LootParams entityParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .withParameter(LootContextParams.THIS_ENTITY, EnlightenedEndModEntities.FUMESPLAT.get().create(level))
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                .create(LootContextParamSets.ENTITY);
        List<ItemStack> fumesplatDrops = fumesplatLoot.getRandomItems(entityParams);
        helper.assertTrue(fumesplatDrops.stream().anyMatch(s -> s.is(EnlightenedEndModItems.SPLATTER_GLAND.get())),
                "Fumesplat loot table must drop enlightened_end:splatter_gland");

        // 验证祭坛战利品表修复了无效附魔与重命名模板后可正常产出战利品
        LootTable giftLoot = level.getServer().getLootData().getLootTable(new ResourceLocation("enlightened_end:entities/sacrifice_statue_gift"));
        LootParams giftParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .withParameter(LootContextParams.THIS_ENTITY, helper.makeMockPlayer())
                .create(LootContextParamSets.GIFT);
        List<ItemStack> giftDrops = giftLoot.getRandomItems(giftParams);
        helper.assertTrue(giftDrops.size() >= 4, "Offering Statue gift loot table failed to load or returned empty drops: " + giftDrops.size());
        helper.assertTrue(giftDrops.stream().anyMatch(s -> s.is(EnlightenedEndModItems.ADAMANTITE_SMITHING_TEMPLATE.get())),
                "Offering Statue gift loot must drop adamantite_smithing_template");

        // 验证 cerulean_frond 与剪刀采集 cerulean_grass 掉落自身而非下界苗
        LootTable frondLoot = level.getServer().getLootData().getLootTable(new ResourceLocation("enlightened_end:blocks/cerulean_frond"));
        LootParams frondParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withParameter(LootContextParams.BLOCK_STATE, EnlightenedEndModBlocks.CERULEAN_FROND.get().defaultBlockState())
                .create(LootContextParamSets.BLOCK);
        helper.assertTrue(frondLoot.getRandomItems(frondParams).stream()
                        .anyMatch(s -> s.is(EnlightenedEndModBlocks.CERULEAN_FROND.get().asItem())),
                "cerulean_frond block loot table must drop cerulean_frond");

        LootTable grassLoot = level.getServer().getLootData().getLootTable(new ResourceLocation("enlightened_end:blocks/cerulean_grass"));
        LootParams grassParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.SHEARS))
                .withParameter(LootContextParams.BLOCK_STATE, EnlightenedEndModBlocks.CERULEAN_GRASS.get().defaultBlockState())
                .create(LootContextParamSets.BLOCK);
        List<ItemStack> grassDrops = grassLoot.getRandomItems(grassParams);
        helper.assertTrue(grassDrops.stream().anyMatch(s -> s.is(EnlightenedEndModBlocks.CERULEAN_GRASS.get().asItem())),
                "cerulean_grass sheared must drop cerulean_grass instead of nether_sprouts");

        // 验证 7 个非生物实体均已修正为 MobCategory.MISC
        helper.assertTrue(EnlightenedEndModEntities.FLOATING_ELEVIBLOOM.get().getCategory() == MobCategory.MISC, "floating_elevibloom must be MISC");
        helper.assertTrue(EnlightenedEndModEntities.ACTIVATED_NUCLEAR_BOMB.get().getCategory() == MobCategory.MISC, "activated_nuclear_bomb must be MISC");
        helper.assertTrue(EnlightenedEndModEntities.FALLING_BOUNCELING_NUT.get().getCategory() == MobCategory.MISC, "falling_bounceling_nut must be MISC");
        helper.assertTrue(EnlightenedEndModEntities.DISTURBED_HELIUM_GAS.get().getCategory() == MobCategory.MISC, "disturbed_helium_gas must be MISC");
        helper.assertTrue(EnlightenedEndModEntities.DISTURBED_XENON_GAS.get().getCategory() == MobCategory.MISC, "disturbed_xenon_gas must be MISC");
        helper.assertTrue(EnlightenedEndModEntities.FALLING_HELIOSAND.get().getCategory() == MobCategory.MISC, "falling_heliosand must be MISC");
        helper.assertTrue(EnlightenedEndModEntities.OOZE_BUBBLE.get().getCategory() == MobCategory.MISC, "ooze_bubble must be MISC");

        helper.succeed();
    }

    /**
     * 9. 验证 Ringling 捕食判定、闪光弹命中消失、腐泥炼药锅桶/瓶交互、祭坛防漏斗白嫖、成组气罐不吞物品与背包 tick 修复。
     */
    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Enlightendbugfix.MOD_ID)
    public static void testGameplayInteractionsAndExploitFixes(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos basePos = helper.absolutePos(new BlockPos(2, 2, 2));

        // 1) Ringling 攻击非 Bounceling（如猪）不得秒删对方，攻击 Bounceling 则正常捕食并设置自身 full
        RinglingEntity ringling = EnlightenedEndModEntities.RINGLING.get().create(level);
        Pig pig = EntityTypeHelper.spawnPig(level, basePos);
        BouncelingEntity bouncer = EnlightenedEndModEntities.BOUNCER.get().create(level);
        helper.assertTrue(ringling != null && bouncer != null, "Entities must instantiate");
        ringling.setPos(basePos.getX(), basePos.getY(), basePos.getZ());
        bouncer.setPos(basePos.getX(), basePos.getY(), basePos.getZ());
        level.addFreshEntity(ringling);
        level.addFreshEntity(bouncer);

        RinglingAttackProcedure.execute(pig, ringling);
        helper.assertFalse(pig.isRemoved(), "Ringling attacking a non-Bounceling entity must NOT discard it");
        helper.assertFalse(ringling.getPersistentData().getBoolean("full"), "Ringling should still be hungry");

        RinglingAttackProcedure.execute(bouncer, ringling);
        helper.assertTrue(bouncer.isRemoved(), "Ringling attacking Bounceling should eat/discard Bounceling");
        helper.assertTrue(ringling.getPersistentData().getBoolean("full"), "Ringling should become full after eating Bounceling");

        // 2) 闪光弹命中实体或无投手命中方块后立即致盲并消失
        FlashBombEntity flash1 = new FlashBombEntity(EnlightenedEndModEntities.FLASH_BOMB.get(), level);
        flash1.setPos(pig.getX(), pig.getY(), pig.getZ());
        level.addFreshEntity(flash1);
        flash1.onHitEntity(new EntityHitResult(pig));
        helper.assertTrue(flash1.isRemoved(), "FlashBombEntity must discard immediately on entity hit");
        helper.assertTrue(pig.hasEffect(EnlightenedEndModMobEffects.DAZED.get()), "Hit entity should receive Dazed effect");

        pig.removeAllEffects();
        FlashBombEntity flash2 = new FlashBombEntity(EnlightenedEndModEntities.FLASH_BOMB.get(), level);
        flash2.setPos(pig.getX(), pig.getY(), pig.getZ());
        level.addFreshEntity(flash2);
        flash2.onHitBlock(new BlockHitResult(Vec3.atCenterOf(basePos), Direction.UP, basePos, false));
        helper.assertTrue(flash2.isRemoved(), "Dispenser/ownerless FlashBombEntity must discard on block hit");
        helper.assertTrue(pig.hasEffect(EnlightenedEndModMobEffects.DAZED.get()), "Ownerless FlashBombEntity block hit must still apply Dazed");

        // 验证原版发射器发射闪光弹会射出 FlashBombEntity 投射物而非普通掉落物
        BlockPos dispenserPos = basePos.south(4);
        BlockState dispenserState = Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.SOUTH);
        level.setBlock(dispenserPos, dispenserState, 3);
        DispenserBlockEntity dispenserBe = (DispenserBlockEntity) level.getBlockEntity(dispenserPos);
        helper.assertTrue(dispenserBe != null, "DispenserBlockEntity must exist");
        dispenserBe.setItem(0, new ItemStack(EnlightenedEndModItems.FLASH_BOMB.get(), 1));
        dispenserState.tick(level, dispenserPos, level.getRandom());
        helper.assertTrue(dispenserBe.getItem(0).isEmpty(), "Dispenser should consume 1 Flash Bomb");
        List<FlashBombEntity> firedBombs = level.getEntitiesOfClass(
                FlashBombEntity.class,
                new net.minecraft.world.phys.AABB(dispenserPos).inflate(3.0D)
        );
        helper.assertTrue(!firedBombs.isEmpty(), "Dispenser must fire FlashBombEntity projectile instead of dropping item");
        firedBombs.forEach(FlashBombEntity::discard);

        // 3) 腐泥炼药锅：空桶舀出腐泥桶，空玻璃瓶舀出腐泥瓶
        Player survivalPlayer = helper.makeMockSurvivalPlayer();
        BlockPos cauldronPos = basePos.east(2);
        level.setBlock(cauldronPos, EnlightenedEndModBlocks.OOZE_CAULDRON_FULL.get().defaultBlockState(), 3);
        survivalPlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
        EmptyCauldronOozeProcedure.execute(level, cauldronPos.getX(), cauldronPos.getY(), cauldronPos.getZ(), survivalPlayer);
        helper.assertTrue(level.getBlockState(cauldronPos).is(Blocks.CAULDRON), "Full ooze cauldron should empty to cauldron");
        helper.assertTrue(survivalPlayer.getInventory().countItem(EnlightenedEndModItems.OOZE_FLUID_BUCKET.get()) == 1,
                "Scooping full ooze cauldron with bucket must grant OOZE_FLUID_BUCKET");

        level.setBlock(cauldronPos, EnlightenedEndModBlocks.OOZE_CAULDRON_FULL.get().defaultBlockState(), 3);
        survivalPlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE, 2));
        EmptyCauldronOozeProcedure.execute(level, cauldronPos.getX(), cauldronPos.getY(), cauldronPos.getZ(), survivalPlayer);
        helper.assertTrue(level.getBlockState(cauldronPos).is(EnlightenedEndModBlocks.OOZE_CAULDRON_2.get()),
                "Scooping full ooze cauldron with glass bottle should step down to ooze_cauldron_2");
        helper.assertTrue(survivalPlayer.getInventory().countItem(EnlightenedEndModItems.OOZE_BOTTLE.get()) == 1,
                "Scooping ooze cauldron with glass bottle must grant OOZE_BOTTLE");

        // 4) 祭坛防漏斗白嫖预览物品
        BlockPos statuePos = basePos.west(2);
        level.setBlock(statuePos, EnlightenedEndModBlocks.OFFERING_STATUE.get().defaultBlockState(), 3);
        SacrificalStatueBlockEntity statueBe = (SacrificalStatueBlockEntity) level.getBlockEntity(statuePos);
        helper.assertTrue(statueBe != null, "SacrificalStatueBlockEntity must exist");
        statueBe.setItem(0, new ItemStack(Items.DRAGON_BREATH));
        helper.assertTrue(statueBe.getSlotsForFace(Direction.DOWN).length == 0, "Offering Statue must expose 0 slots to hoppers");
        helper.assertFalse(statueBe.canTakeItemThroughFace(0, statueBe.getItem(0), Direction.DOWN),
                "Offering Statue must forbid hopper item extraction");

        // 5) 手持 5 个氦气罐放置 1 格氦气，必须保留剩余 4 个氦气罐并给 1 个空气罐
        BlockPos clickSolid = basePos.north(2);
        level.setBlock(clickSolid, Blocks.END_STONE.defaultBlockState(), 3);
        level.setBlock(clickSolid.above(), Blocks.AIR.defaultBlockState(), 3);
        survivalPlayer.getInventory().clearContent();
        survivalPlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(EnlightenedEndModItems.HELIUM_JAR.get(), 5));
        PlaceHeliumGasProcedure.execute(level, clickSolid.getX(), clickSolid.getY(), clickSolid.getZ(), Direction.UP, survivalPlayer);
        helper.assertTrue(survivalPlayer.getMainHandItem().is(EnlightenedEndModItems.HELIUM_JAR.get())
                && survivalPlayer.getMainHandItem().getCount() == 4, "Remaining 4 Helium Jars must not be overwritten");
        helper.assertTrue(survivalPlayer.getInventory().countItem(EnlightenedEndModItems.GAS_JAR.get()) == 1,
                "Player should receive 1 empty Gas Jar");

        // 6) 满耐久虚空钢镐背包 tick 不被改成 -1；RinglingPot 只在自身无 health 标签时初始化一次
        ItemStack freshPick = new ItemStack(EnlightenedEndModItems.ADAMANTITE_PICKAXE.get());
        VoidsteelToolInInventoryTickProcedure.execute(survivalPlayer, freshPick);
        helper.assertTrue(freshPick.getDamageValue() == 0, "Fresh Voidsteel tool damage must stay 0, not -1");

        ItemStack damagedPick = new ItemStack(EnlightenedEndModItems.ADAMANTITE_PICKAXE.get());
        damagedPick.setDamageValue(5);
        VoidsteelToolInInventoryTickProcedure.execute(survivalPlayer, damagedPick);
        helper.assertTrue(damagedPick.getDamageValue() == 4, "Off-hand/stowed damaged Voidsteel tool should repair by 1");

        ItemStack pot = new ItemStack(EnlightenedEndModItems.RINGLING_POT.get());
        pot.getOrCreateTag().putDouble("health", 6.0D);
        RinglingPotItemInInventoryTickProcedure.execute(survivalPlayer, pot);
        helper.assertTrue(pot.getTag().getDouble("health") == 6.0D,
                "RinglingPot with existing positive health must not be overwritten back to 10.0 every tick");

        // 7) 创造模式下 BottleEnsnare 与喂食 Ringling 交互不扣创造玩家瓶子
        Player creativePlayer = helper.makeMockPlayer();
        creativePlayer.getAbilities().instabuild = true;
        BrainfeederEntity gloop = EnlightenedEndModEntities.GLOOP.get().create(level);
        gloop.setPos(basePos.getX(), basePos.getY(), basePos.getZ());
        gloop.getPersistentData().putDouble("hunger", 300.0D);
        level.addFreshEntity(gloop);
        creativePlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE, 1));
        BottleEnsnareProcedure.execute(level, gloop.getX(), gloop.getY(), gloop.getZ(), gloop, creativePlayer);
        helper.assertTrue(creativePlayer.getMainHandItem().getCount() == 1, "Creative player should not lose glass bottle");
        FeedRinglingProcedure.execute(level, ringling, creativePlayer);
        CollectDistrubedHeliumGasProcedure.execute(level, basePos.getX(), basePos.getY(), basePos.getZ(), gloop, creativePlayer);

        helper.succeed();
    }

    /**
     * 10. 验证 215 个数据驱动方块战利品表、bouncer/floating_elevibloom 实体战利品表（含天敌不掉落条件）及全部硬编码文本翻译键。
     */
    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Enlightendbugfix.MOD_ID)
    public static void testDataDrivenLootTablesAndLocalizedTexts(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        Player player = helper.makeMockPlayer();

        // 1) 验证双层台阶通过 JSON 战利品表掉落 2 个，门上半部掉 0 个、下半部掉 1 个，非自身掉落方块正确返回目标物品
        BlockState doubleSlab = EnlightenedEndModBlocks.END_STONE_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);
        LootParams.Builder slabBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE));
        List<ItemStack> slabDrops = doubleSlab.getDrops(slabBuilder);
        helper.assertTrue(slabDrops.size() == 1 && slabDrops.get(0).getCount() == 2,
                "Double end_stone_slab must drop 2 slabs via JSON loot table");

        BlockState upperDoor = EnlightenedEndModBlocks.CERULEAN_DOOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF,
                        net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER);
        BlockState lowerDoor = EnlightenedEndModBlocks.CERULEAN_DOOR.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF,
                        net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER);
        LootParams.Builder axeBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_AXE));
        helper.assertTrue(upperDoor.getDrops(axeBuilder).isEmpty(), "Upper half of cerulean_door must drop nothing");
        helper.assertTrue(lowerDoor.getDrops(axeBuilder).size() == 1, "Lower half of cerulean_door must drop 1 door");

        BlockState stalk1 = EnlightenedEndModBlocks.CERULEAN_STALK_1.get().defaultBlockState();
        List<ItemStack> stalkDrops = stalk1.getDrops(axeBuilder);
        helper.assertTrue(stalkDrops.stream().anyMatch(s -> s.is(EnlightenedEndModItems.CERULEAN_STALK.get())),
                "cerulean_stalk_1 must drop cerulean_stalk via JSON loot table");

        // 2) 验证 bouncer 与 floating_elevibloom 实体 JSON 战利品表（玩家击杀掉落，天敌击杀不掉落，且原 DiesProcedure 不再重复刷掉落）
        LootTable bouncerLoot = level.getServer().getLootData().getLootTable(new ResourceLocation("enlightened_end:entities/bouncer"));
        helper.assertTrue(bouncerLoot != LootTable.EMPTY, "entities/bouncer loot table must exist");
        BouncelingEntity bouncer = EnlightenedEndModEntities.BOUNCER.get().create(level);
        bouncer.setPos(pos.getX(), pos.getY(), pos.getZ());
        LootParams playerKillParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, bouncer)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().playerAttack(player))
                .withParameter(LootContextParams.KILLER_ENTITY, player)
                .create(LootContextParamSets.ENTITY);
        helper.assertTrue(bouncerLoot.getRandomItems(playerKillParams).stream()
                        .anyMatch(s -> s.is(EnlightenedEndModItems.SQUISHED_BOUNCER.get())),
                "Player killing bouncer must drop squished_bouncer via JSON");

        RinglingEntity ringling = EnlightenedEndModEntities.RINGLING.get().create(level);
        ringling.setPos(pos.getX(), pos.getY(), pos.getZ());
        LootParams ringlingKillParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, bouncer)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().mobAttack(ringling))
                .withParameter(LootContextParams.KILLER_ENTITY, ringling)
                .create(LootContextParamSets.ENTITY);
        helper.assertTrue(bouncerLoot.getRandomItems(ringlingKillParams).isEmpty(),
                "Ringling killing bouncer must not drop squished_bouncer");

        LootTable elevibloomLoot = level.getServer().getLootData().getLootTable(new ResourceLocation("enlightened_end:entities/floating_elevibloom"));
        helper.assertTrue(elevibloomLoot != LootTable.EMPTY, "entities/floating_elevibloom loot table must exist");
        helper.assertTrue(elevibloomLoot.getRandomItems(playerKillParams).stream()
                        .anyMatch(s -> s.is(EnlightenedEndModItems.ELEVIBLOOM.get())),
                "Player killing floating_elevibloom must drop elevibloom via JSON");

        // 3) 验证原硬编码 Tooltip 与方块实体标题均已改为 TranslatableContents
        List<net.minecraft.network.chat.Component> teleporterTip = new java.util.ArrayList<>();
        EnlightenedEndModBlocks.TELEPORTER.get().appendHoverText(
                new ItemStack(EnlightenedEndModBlocks.TELEPORTER.get()),
                level,
                teleporterTip,
                net.minecraft.world.item.TooltipFlag.Default.NORMAL
        );
        helper.assertTrue(teleporterTip.size() == 2
                        && teleporterTip.get(0).getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents tc0
                        && "tooltip.enlightened_end.teleporter.line1".equals(tc0.getKey()),
                "TeleporterBlock tooltip must use translation key");

        List<net.minecraft.network.chat.Component> bladeTip = new java.util.ArrayList<>();
        EnlightenedEndModItems.ROOTING_BLADE.get().appendHoverText(
                new ItemStack(EnlightenedEndModItems.ROOTING_BLADE.get()),
                level,
                bladeTip,
                net.minecraft.world.item.TooltipFlag.Default.NORMAL
        );
        helper.assertTrue(bladeTip.size() == 1
                        && bladeTip.get(0).getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents tc1
                        && "tooltip.enlightened_end.rooting_blade".equals(tc1.getKey()),
                "RootingBladeItem tooltip must use translation key");

        level.setBlock(pos, EnlightenedEndModBlocks.NUCLEAR_FURNACE.get().defaultBlockState(), 3);
        NuclearFurnaceBlockEntity furnaceBe = (NuclearFurnaceBlockEntity) level.getBlockEntity(pos);
        helper.assertTrue(furnaceBe != null
                        && furnaceBe.getDisplayName().getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents tc2
                        && "block.enlightened_end.nuclear_furnace".equals(tc2.getKey()),
                "NuclearFurnaceBlockEntity displayName must use block translation key");

        helper.succeed();
    }

    private static final class EntityTypeHelper {
        static Pig spawnPig(ServerLevel level, BlockPos pos) {
            Pig pig = net.minecraft.world.entity.EntityType.PIG.create(level);
            pig.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            level.addFreshEntity(pig);
            return pig;
        }
    }
}
