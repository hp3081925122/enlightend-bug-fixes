package com.hp.enlightendbugfix.mixin;

import javax.annotation.Nullable;
import net.mcreator.enlightened_end.block.entity.SacrificalStatueBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复祭坛（Offering Statue / SacrificalStatueBlockEntity）将贡品预览槽暴露给漏斗/管道导致无限白嫖龙息、幽匿尖啸体等珍稀物品的漏洞。
 */
@Mixin(SacrificalStatueBlockEntity.class)
public abstract class SacrificalStatueBlockEntityMixin {
    @Unique
    private static final int[] EMPTY_SLOTS = new int[0];

    @Inject(method = "getSlotsForFace", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$hidePreviewSlotFromAutomation(Direction side, CallbackInfoReturnable<int[]> cir) {
        cir.setReturnValue(EMPTY_SLOTS);
    }

    @Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$disallowAutomationInsert(int index, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "canPlaceItemThroughFace", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$disallowFaceInsert(int index, ItemStack stack, @Nullable Direction direction, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "canTakeItemThroughFace", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$disallowFaceExtract(int index, ItemStack stack, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
