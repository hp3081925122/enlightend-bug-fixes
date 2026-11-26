package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.block.HeliumGasBlock;
import net.mcreator.enlightened_end.block.XenonGasBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复氦气（HeliumGasBlock）与氙气（XenonGasBlock）漏设 noOcclusion 且阻光 15 导致贴靠实心方块时剔除相邻方块面的透视穿模问题。
 */
@Mixin(value = {HeliumGasBlock.class, XenonGasBlock.class})
public abstract class GasBlocksOcclusionMixin extends Block {
    protected GasBlocksOcclusionMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"
            )
    )
    private static BlockBehaviour.Properties enlightendbugfix$enableNoOcclusion(BlockBehaviour.Properties properties) {
        return properties.noOcclusion().isViewBlocking((bs, br, bp) -> false).isSuffocating((bs, br, bp) -> false);
    }

    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$transparentLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}
