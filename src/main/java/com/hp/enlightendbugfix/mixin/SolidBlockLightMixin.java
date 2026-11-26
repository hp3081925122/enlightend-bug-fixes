package com.hp.enlightendbugfix.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复 12 个不透明石头台阶、楼梯与围墙覆写 getLightBlock 恒返回 0 导致的异常透光问题。
 */
@Mixin(value = {
        net.mcreator.enlightened_end.block.EndStoneSlabBlock.class,
        net.mcreator.enlightened_end.block.EndStoneStairsBlock.class,
        net.mcreator.enlightened_end.block.EndStoneWallBlock.class,
        net.mcreator.enlightened_end.block.NullstoneBrickSlabBlock.class,
        net.mcreator.enlightened_end.block.NullstoneBrickStairsBlock.class,
        net.mcreator.enlightened_end.block.NullstoneBrickWallBlock.class,
        net.mcreator.enlightened_end.block.NullstoneSlabBlock.class,
        net.mcreator.enlightened_end.block.NullstoneStairsBlock.class,
        net.mcreator.enlightened_end.block.NullstoneWallBlock.class,
        net.mcreator.enlightened_end.block.PolishedNullstoneSlabBlock.class,
        net.mcreator.enlightened_end.block.PolishedNullstoneStairsBlock.class,
        net.mcreator.enlightened_end.block.PolishedNullstoneWallBlock.class
})
public abstract class SolidBlockLightMixin extends Block {
    protected SolidBlockLightMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$restoreVanillaLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(super.getLightBlock(state, worldIn, pos));
    }
}
