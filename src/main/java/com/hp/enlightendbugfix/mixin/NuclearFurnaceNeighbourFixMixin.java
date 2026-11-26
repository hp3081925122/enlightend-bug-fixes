package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.procedures.NuclearFurnaceNeighbourBlockChangesProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 NuclearFurnaceNeighbourBlockChangesProcedure 在检测上/下方漏斗时把 X 坐标错写成 Z 坐标（BlockPos.containing(z, y±1, z)），
 * 导致远程破坏并无限复制 (z, y±1, z) 处任意方块（含基岩）的恶性漏洞。
 */
@Mixin(value = NuclearFurnaceNeighbourBlockChangesProcedure.class, remap = false)
public abstract class NuclearFurnaceNeighbourFixMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$fixHopperBreakCoordinates(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockPos[] neighbors = new BlockPos[]{
                BlockPos.containing(x + 1.0D, y, z),
                BlockPos.containing(x - 1.0D, y, z),
                BlockPos.containing(x, y, z - 1.0D),
                BlockPos.containing(x, y, z + 1.0D),
                BlockPos.containing(x, y + 1.0D, z),
                BlockPos.containing(x, y - 1.0D, z)
        };
        for (BlockPos neighborPos : neighbors) {
            if (world.getBlockState(neighborPos).getBlock() == Blocks.HOPPER) {
                Block.dropResources(world.getBlockState(neighborPos), world, neighborPos, null);
                world.destroyBlock(neighborPos, false);
            }
        }
        ci.cancel();
    }
}
