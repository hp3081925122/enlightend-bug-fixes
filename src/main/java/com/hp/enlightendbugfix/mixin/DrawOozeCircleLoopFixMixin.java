package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.procedures.DrawOozeCircleProcedure;
import net.mcreator.enlightened_end.procedures.PlantOozePoolPlantsProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 修复 MCR-54：将 DrawOozeCircleProcedure 循环条件内动态 Mth.nextInt(RandomSource.create(), 2, 5) 提前到循环外，缓存替换标签并复用世界随机源
@Mixin(value = DrawOozeCircleProcedure.class, remap = false)
public abstract class DrawOozeCircleLoopFixMixin {
    @Unique
    private static final TagKey<Block> EF_FIX$REPLACE_VALID = BlockTags.create(new ResourceLocation("enlightened_end:end_biome_replace_valid"));

    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDD)V", at = @At("HEAD"), cancellable = true)
    private static void efFix$hoistOozeCircleLayers(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        ci.cancel();
        if (world == null || world.isClientSide()) {
            return;
        }
        RandomSource random = world.getRandom();
        double circleSize = Mth.nextInt(random, 3, 6);
        int layers = Mth.nextInt(random, 2, 5);
        Block oozeBlock = EnlightenedEndModBlocks.OOZE_FLUID.get();
        BlockState oozeState = oozeBlock.defaultBlockState();
        BlockState chorloamState = EnlightenedEndModBlocks.CHORLOAM.get().defaultBlockState();

        for (int layer = 0; layer < layers; layer++) {
            for (double rIdx = 0.0; rIdx <= circleSize; rIdx++) {
                double radius = rIdx + 1.0;
                for (int deg = 0; deg <= 360; deg++) {
                    double x2 = radius * Math.cos(deg);
                    double z2 = radius * Math.sin(deg);
                    BlockPos pos = BlockPos.containing(x + x2, y - layer, z + z2);
                    BlockPos above = pos.above();
                    BlockState curState = world.getBlockState(pos);
                    if (!curState.is(EF_FIX$REPLACE_VALID)) {
                        continue;
                    }
                    boolean aboveAir = world.isEmptyBlock(above);
                    boolean aboveOoze = !aboveAir && world.getFluidState(above).createLegacyBlock().is(oozeBlock);
                    if (!world.isEmptyBlock(pos)
                            && (aboveAir || aboveOoze)
                            && !world.isEmptyBlock(pos.east())
                            && !world.isEmptyBlock(pos.west())
                            && !world.isEmptyBlock(pos.north())
                            && !world.isEmptyBlock(pos.south())
                            && !world.isEmptyBlock(pos.below())) {
                        world.setBlock(pos, oozeState, 3);
                    } else if (aboveAir) {
                        world.setBlock(pos, chorloamState, 3);
                        PlantOozePoolPlantsProcedure.execute(world, x + x2, y - layer + 1.0, z + z2);
                    }
                }
            }
            circleSize -= Mth.nextInt(random, 1, 2);
        }
    }
}
