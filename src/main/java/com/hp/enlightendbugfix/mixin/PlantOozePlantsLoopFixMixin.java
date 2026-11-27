package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.procedures.PlantOozePlantsProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 修复 MCR-54：将 PlantOozePlantsProcedure 气孔柱高度随机循环条件 Mth.nextInt(RandomSource.create(), 2, 10) 提前到循环外，修复高度分布严重偏矮
@Mixin(value = PlantOozePlantsProcedure.class, remap = false)
public abstract class PlantOozePlantsLoopFixMixin {
    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDD)V", at = @At("HEAD"), cancellable = true)
    private static void efFix$hoistGasVentHeight(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        ci.cancel();
        if (world == null || world.isClientSide()) {
            return;
        }
        RandomSource random = world.getRandom();
        BlockPos base = BlockPos.containing(x, y, z);
        BlockPos above = base.above();
        if (!world.isEmptyBlock(above)) {
            return;
        }

        if (Mth.nextInt(random, 1, 16) == 1) {
            world.setBlock(above, EnlightenedEndModBlocks.OOZE_TUBES.get().defaultBlockState(), 3);
        } else if (Mth.nextInt(random, 1, 16) == 1) {
            world.setBlock(above, EnlightenedEndModBlocks.GAS_VENT_BOTTOM.get().defaultBlockState(), 3);
            int done = 1;
            int maxSegments = Mth.nextInt(random, 2, 10);
            for (int i = 0; i < maxSegments; i++) {
                BlockPos segPos = base.above(done + 1);
                if (!world.isEmptyBlock(segPos)) {
                    break;
                }
                done++;
                world.setBlock(segPos, EnlightenedEndModBlocks.GAS_VENT_MIDDLE.get().defaultBlockState(), 3);
            }

            if (Mth.nextInt(random, 1, 3) > 1
                    && !world.isEmptyBlock(base.below())
                    && !world.isEmptyBlock(base.north())
                    && !world.isEmptyBlock(base.south())
                    && !world.isEmptyBlock(base.east())
                    && !world.isEmptyBlock(base.west())) {
                world.setBlock(base.below(), EnlightenedEndModBlocks.OOZE_FLUID.get().defaultBlockState(), 3);
            }

            BlockPos topPos = base.above(done);
            if (world.getBlockState(topPos).is(EnlightenedEndModBlocks.GAS_VENT_MIDDLE.get())) {
                if (world.getFluidState(base.below()).createLegacyBlock().is(EnlightenedEndModBlocks.OOZE_FLUID.get())) {
                    world.setBlock(topPos, EnlightenedEndModBlocks.GAS_VENT_OOZE.get().defaultBlockState(), 3);
                } else {
                    world.setBlock(topPos, EnlightenedEndModBlocks.GAS_VENT.get().defaultBlockState(), 3);
                }
            }
        }
    }
}
