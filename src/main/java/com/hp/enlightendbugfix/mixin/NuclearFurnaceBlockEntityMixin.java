package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.block.entity.NuclearFurnaceBlockEntity;
import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复核熔炉容器槽位自动化规则：0 号燃料槽仅允许放入铱棒，侧面/下方抽取仅允许从 4~6 号产物槽取出成品。
 */
@Mixin(NuclearFurnaceBlockEntity.class)
public abstract class NuclearFurnaceBlockEntityMixin {
    @Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$restrictFuelAndOutputSlots(int index, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (index == 0) {
            cir.setReturnValue(!stack.isEmpty() && stack.getItem() == EnlightenedEndModItems.IRRADIUM_BAR.get());
        } else if (index >= 4) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canTakeItemThroughFace", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$extractOutputsOnly(int index, ItemStack stack, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(index >= 4 && index <= 6);
    }
}
