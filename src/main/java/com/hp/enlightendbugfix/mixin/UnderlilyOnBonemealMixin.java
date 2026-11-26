package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.procedures.UnderlilyOnBonemealProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复骨粉催熟 Underlily 时未检查正下方 (x, y - 1, z) 是否为空气，导致直接覆盖销毁下方基岩、箱子等任意方块的漏洞。
 */
@Mixin(value = UnderlilyOnBonemealProcedure.class, remap = false)
public abstract class UnderlilyOnBonemealMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$guardBelowBlockBeforeReplace(
            LevelAccessor world, double x, double y, double z, CallbackInfo ci
    ) {
        if (world == null) {
            ci.cancel();
            return;
        }
        RandomSource random = RandomSource.create();
        BlockPos directBelow = BlockPos.containing(x, y - 1.0D, z);
        if (world.isEmptyBlock(directBelow)) {
            Block chosen = random.nextInt(5) == 0
                    ? EnlightenedEndModBlocks.CRADLING_FLOWER.get()
                    : EnlightenedEndModBlocks.CERULEAN_GRASS.get();
            world.setBlock(directBelow, chosen.defaultBlockState(), 3);
        }

        for (int dx = -3; dx < 3; dx++) {
            for (int dy = -3; dy < 3; dy++) {
                for (int dz = -3; dz < 3; dz++) {
                    BlockPos checkPos = BlockPos.containing(x + dx, y + dy, z + dz);
                    BlockPos belowPos = checkPos.below();
                    Block current = world.getBlockState(checkPos).getBlock();
                    if (current == EnlightenedEndModBlocks.UNDERLILY.get() && world.isEmptyBlock(belowPos) && random.nextInt(4) == 0) {
                        Block plant = random.nextInt(5) == 0
                                ? EnlightenedEndModBlocks.CRADLING_FLOWER.get()
                                : EnlightenedEndModBlocks.CERULEAN_GRASS.get();
                        world.setBlock(belowPos, plant.defaultBlockState(), 3);
                    } else if (current == Blocks.END_STONE && world.isEmptyBlock(belowPos) && random.nextInt(8) == 0) {
                        world.setBlock(checkPos, EnlightenedEndModBlocks.UNDERLILY.get().defaultBlockState(), 3);
                    }
                }
            }
        }
        ci.cancel();
    }
}
