package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.DepletedIrradiumUpdateTickProcedure;
import net.mcreator.enlightened_end.procedures.GlaciumBlockUpdateTickProcedure;
import net.mcreator.enlightened_end.procedures.RadiationEmissionProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 优化辐射方块、贫铀方块与冰晶方块的周围实体扫描：
 * 1. 先计算方块状态（例如空闲核熔炉辐射为 0 时直接返回，不再做每秒 20 次 16x16x16 范围全实体扫描与排序）；
 * 2. 将 Entity.class + Stream.sorted 替换为 LivingEntity.class 无排序直接遍历，静态缓存全部标签键；
 * 3. 支持通过配置文件调节扫描半径并节流网络包。
 */
@Mixin(value = {
        RadiationEmissionProcedure.class,
        DepletedIrradiumUpdateTickProcedure.class,
        GlaciumBlockUpdateTickProcedure.class
}, remap = false)
public abstract class BlockEntityScanOptimizeMixin {
    @Unique
    private static final TagKey<EntityType<?>> IMMUNE_TO_RADIATION = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge:immune_to_radiation"));
    @Unique
    private static final TagKey<Block> BLOCK_EXTREME_RAD = BlockTags.create(new ResourceLocation("enlightened_end:extreme_radioactive"));
    @Unique
    private static final TagKey<Block> BLOCK_HIGH_RAD = BlockTags.create(new ResourceLocation("enlightened_end:high_radioactive"));
    @Unique
    private static final TagKey<Block> BLOCK_MEDIUM_RAD = BlockTags.create(new ResourceLocation("enlightened_end:medium_radioactive"));
    @Unique
    private static final TagKey<Block> BLOCK_LOW_RAD = BlockTags.create(new ResourceLocation("enlightened_end:low_radioactive"));
    @Unique
    private static final TagKey<Block> DEPLETED_BLOCKS_CHECK = BlockTags.create(new ResourceLocation("enlightened_end:depleted_irradium"));
    @Unique
    private static final ResourceLocation ADV_DEPLETED = new ResourceLocation("enlightened_end:depleted_irradium_advancement");
    @Unique
    private static final boolean THERMAL_LOADED = ModList.get().isLoaded("thermal");

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$optimizedBlockEntityScan(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!(world instanceof Level level) || level.isClientSide()) {
            ci.cancel();
            return;
        }

        BlockPos pos = BlockPos.containing(x, y, z);
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // 分支 1：冰晶方块及台阶/楼梯/围墙（1 格半径冰冻）
        if (block == EnlightenedEndModBlocks.GLACIUM_BLOCK.get()
                || block == EnlightenedEndModBlocks.GLACIUM_BRICKS.get()
                || block == EnlightenedEndModBlocks.GLACIUM_BRICK_SLAB.get()
                || block == EnlightenedEndModBlocks.GLACIUM_BRICK_STAIRS.get()
                || block == EnlightenedEndModBlocks.GLACIUM_BRICK_WALL.get()) {
            Vec3 center = new Vec3(x + 0.5D, y + 0.5D, z + 0.5D);
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(1.0D))) {
                if (entity.getTicksFrozen() <= 300 && entity.getRemainingFireTicks() <= 0) {
                    entity.setTicksFrozen(entity.getTicksFrozen() + 15);
                }
            }
            ci.cancel();
            return;
        }

        double radius = EfBugfixConfig.RADIATION_SCAN_RADIUS.get();

        // 分支 2：贫铀系列方块（吸收周围实体辐射）
        if (block == EnlightenedEndModBlocks.DEPLETED_IRRADIUM_BLOCK.get()
                || block == EnlightenedEndModBlocks.DEPLETED_IRRADIUM_BRICKS.get()
                || block == EnlightenedEndModBlocks.DEPLETED_IRRADIUM_BRICK_SLAB.get()
                || block == EnlightenedEndModBlocks.DEPLETED_IRRADIUM_BRICK_STAIRS.get()
                || block == EnlightenedEndModBlocks.DEPLETED_IRRADIUM_BRICK_WALL.get()) {
            Vec3 center = new Vec3(x, y, z);
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(radius))) {
                if (entity instanceof Player player) {
                    EnlightenedEndModVariables.PlayerVariables vars = EfSafeEntityHelper.getMutablePlayerVars(player);
                    if (vars != null && vars.radiation > 0.0D) {
                        int prevBar = (int) (vars.radiation / 650.0D);
                        vars.radiation = Math.max(0.0D, vars.radiation - 4.0D);
                        int nextBar = (int) (vars.radiation / 650.0D);
                        if (vars.radiation == 0.0D || prevBar != nextBar || player.tickCount % 10 == 0) {
                            vars.syncPlayerVariables(player);
                        }
                    }
                    EfSafeEntityHelper.awardAdvancement(player, ADV_DEPLETED);
                } else {
                    double mobRad = entity.getPersistentData().getDouble("radiation");
                    if (mobRad > 0.0D) {
                        entity.getPersistentData().putDouble("radiation", Math.max(0.0D, mobRad - 4.0D));
                    }
                }
            }
            ci.cancel();
            return;
        }

        // 分支 3：释放辐射的方块（先求方块基础辐射值，为 0 则立即返回，不扫描实体）
        double baseRads = 0.0D;
        if (block == EnlightenedEndModBlocks.NUCLEAR_FURNACE.get()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null && be.getPersistentData().getDouble("fuelTime") > 0.0D) {
                baseRads = 15.0D;
            }
        } else if (state.is(BLOCK_EXTREME_RAD)) {
            baseRads = 12.0D;
        } else if (state.is(BLOCK_HIGH_RAD)) {
            baseRads = 9.0D;
        } else if (state.is(BLOCK_MEDIUM_RAD)) {
            baseRads = 6.0D;
        } else if (state.is(BLOCK_LOW_RAD)) {
            baseRads = 3.0D;
        }

        if (baseRads < 1.0D) {
            ci.cancel();
            return;
        }

        Vec3 center = new Vec3(x, y, z);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(radius))) {
            if (entity.getType().is(IMMUNE_TO_RADIATION)) {
                continue;
            }
            double rads = enlightendbugfix$applyThermalHazmat(entity, baseRads);
            if (rads < 1.0D) {
                continue;
            }

            double dx = entity.getX() - x;
            double dy = entity.getY() - y;
            double dz = entity.getZ() - z;
            double dist = Math.max(1.0D, Math.sqrt(dx * dx + dy * dy + dz * dz) * 0.342D);

            if (entity instanceof Player player) {
                if (EfSafeEntityHelper.isCreative(player) || EfSafeEntityHelper.isSpectator(player)) {
                    continue;
                }
                EnlightenedEndModVariables.PlayerVariables vars = EfSafeEntityHelper.getMutablePlayerVars(player);
                if (vars != null && vars.radiation <= 13650.0D) {
                    boolean wasUnradiated = vars.radiated <= 0.0D;
                    int prevBar = (int) (vars.radiation / 650.0D);
                    vars.radiation = Math.min(13650.0D, vars.radiation + rads / dist);
                    vars.radiated = 40.0D;
                    int nextBar = (int) (vars.radiation / 650.0D);
                    if (wasUnradiated || prevBar != nextBar || player.tickCount % 10 == 0) {
                        vars.syncPlayerVariables(player);
                    }
                }
            } else {
                double mobRad = entity.getPersistentData().getDouble("radiation");
                if (mobRad <= 13650.0D) {
                    entity.getPersistentData().putDouble("radiation", mobRad + rads / dist);
                }
            }
        }

        ci.cancel();
    }

    @Unique
    private static double enlightendbugfix$applyThermalHazmat(LivingEntity entity, double rads) {
        if (!THERMAL_LOADED) {
            return rads;
        }
        Item boots = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:hazmat_boots"));
        Item leggings = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:hazmat_leggings"));
        Item chest = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:hazmat_chestplate"));
        Item helmet = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermal:hazmat_helmet"));
        if (boots != null && entity.getItemBySlot(EquipmentSlot.FEET).getItem() == boots) rads /= 1.5D;
        if (leggings != null && entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == leggings) rads /= 1.5D;
        if (chest != null && entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == chest) rads /= 1.5D;
        if (helmet != null && entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == helmet) rads /= 1.5D;
        return rads;
    }
}
