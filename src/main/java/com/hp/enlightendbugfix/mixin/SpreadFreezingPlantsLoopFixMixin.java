package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.procedures.SpreadFreezingPlantsProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 修复 MCR-54：将 SpreadFreezingPlantsProcedure 全部 3 处循环条件动态随机上限提前到循环外，复用世界随机源并消除高频随机源构造
@Mixin(value = SpreadFreezingPlantsProcedure.class, remap = false)
public abstract class SpreadFreezingPlantsLoopFixMixin {
    private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDD)V", at = @At("HEAD"), cancellable = true)
    private static void efFix$hoistFreezingPlantLoops(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        ci.cancel();
        if (world == null || world.isClientSide()) {
            return;
        }
        RandomSource random = world.getRandom();
        BlockPos base = BlockPos.containing(x, y, z);
        if (!world.isEmptyBlock(base.above())) {
            return;
        }

        if (Mth.nextInt(random, 1, 12) == 1) {
            int fast = 0;
            boolean tall = true;
            int pillarHeight = Mth.nextInt(random, 1, 10);
            for (int i = 0; i < pillarHeight; i++) {
                BlockPos pos = base.above(fast + 1);
                if (!world.isEmptyBlock(pos)) {
                    tall = false;
                    break;
                }
                if (Mth.nextInt(random, 1, 6) == 1) {
                    efFix$placeHorizontalBlock(world, pos, EnlightenedEndModBlocks.FREEZING_TOTEM.get(), random);
                } else {
                    world.setBlock(pos, EnlightenedEndModBlocks.CHISELED_VOID_SHALE.get().defaultBlockState(), 3);
                }
                fast++;
            }
            if (tall && Mth.nextInt(random, 1, 2) == 1) {
                int wallTop = Mth.nextInt(random, 0, 4);
                for (int i = 0; i < wallTop; i++) {
                    BlockPos pos = base.above(fast + 1);
                    if (!world.isEmptyBlock(pos)) {
                        break;
                    }
                    world.setBlock(pos, EnlightenedEndModBlocks.VOID_SHALE_BRICK_WALL.get().defaultBlockState(), 3);
                    fast++;
                }
            }
        } else if (Mth.nextInt(random, 1, 32) == 1) {
            efFix$placeHorizontalBlock(world, base.above(), EnlightenedEndModBlocks.OFFERING_STATUE.get(), random);
        } else if (Mth.nextInt(random, 1, 10) == 1) {
            int fast = 0;
            int wallHeight = Mth.nextInt(random, 0, 4);
            for (int i = 0; i < wallHeight; i++) {
                BlockPos pos = base.above(fast + 1);
                if (!world.isEmptyBlock(pos)) {
                    break;
                }
                world.setBlock(pos, EnlightenedEndModBlocks.VOID_SHALE_BRICK_WALL.get().defaultBlockState(), 3);
                fast++;
            }
        } else if (Mth.nextInt(random, 1, 128) == 1) {
            world.setBlock(base.above(), EnlightenedEndModBlocks.SUPERHEATED_POT.get().defaultBlockState(), 3);
        }
    }

    @Unique
    private static void efFix$placeHorizontalBlock(LevelAccessor world, BlockPos pos, Block block, RandomSource random) {
        BlockState state = block.defaultBlockState();
        Direction dir = HORIZONTAL[random.nextInt(HORIZONTAL.length)];
        if (block.getStateDefinition().getProperty("facing") instanceof DirectionProperty dp && dp.getPossibleValues().contains(dir)) {
            state = state.setValue(dp, dir);
        } else if (block.getStateDefinition().getProperty("axis") instanceof EnumProperty<?> ep && ep.getValueClass() == Direction.Axis.class) {
            @SuppressWarnings("unchecked")
            EnumProperty<Direction.Axis> axisProp = (EnumProperty<Direction.Axis>) ep;
            if (axisProp.getPossibleValues().contains(dir.getAxis())) {
                state = state.setValue(axisProp, dir.getAxis());
            }
        }
        world.setBlock(pos, state, 3);
    }
}
