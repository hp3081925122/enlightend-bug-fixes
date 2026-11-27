package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.procedures.PlanterySystemAirBlockAddedProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 修复 MCR-54：将 PlanterySystemAirBlockAddedProcedure 循环条件内动态 Mth.nextInt(RandomSource.create(), 12, 18) 提前到循环外，复用世界随机源并限幅建筑高度
@Mixin(value = PlanterySystemAirBlockAddedProcedure.class, remap = false)
public abstract class PlanterySystemAirLoopFixMixin {
    @Unique
    private static final ResourceLocation SMALL_END_ISLANDS = new ResourceLocation("small_end_islands");

    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDD)V", at = @At("HEAD"), cancellable = true)
    private static void efFix$hoistPlanetarySystemCount(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        ci.cancel();
        if (world == null || world.isClientSide()) {
            return;
        }
        RandomSource random = world.getRandom();
        int count = Mth.nextInt(random, 12, 18);
        int minY = world.getMinBuildHeight() + 8;
        int maxY = world.getMaxBuildHeight() - 8;

        for (int i = 0; i < count; i++) {
            double ranx = x - Mth.nextInt(random, -64, 64);
            double rany = Mth.clamp(y - Mth.nextInt(random, -16, 16), minY, maxY);
            double ranz = z + Mth.nextInt(random, -64, 64);
            BlockPos target = BlockPos.containing(ranx, rany, ranz);
            if (world.getBiome(target).is(SMALL_END_ISLANDS)) {
                world.setBlock(target, EnlightenedEndModBlocks.PLANETARY_AIR.get().defaultBlockState(), 3);
            }
        }
        world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
    }
}
