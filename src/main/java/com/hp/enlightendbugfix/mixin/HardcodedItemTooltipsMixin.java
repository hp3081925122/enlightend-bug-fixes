package com.hp.enlightendbugfix.mixin;

import java.util.List;
import net.mcreator.enlightened_end.item.EverglintItem;
import net.mcreator.enlightened_end.item.FinalSwordItem;
import net.mcreator.enlightened_end.item.MagicMirrorItem;
import net.mcreator.enlightened_end.item.MalachiteReflectorItem;
import net.mcreator.enlightened_end.item.RootingBladeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 将终末之光 5 个硬编码英文 Tooltip 的物品改为标准翻译键，便于汉化与多语言切换。
 */
@Mixin({
        EverglintItem.class,
        FinalSwordItem.class,
        MagicMirrorItem.class,
        MalachiteReflectorItem.class,
        RootingBladeItem.class
})
public abstract class HardcodedItemTooltipsMixin extends Item {
    protected HardcodedItemTooltipsMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$localizedItemTooltip(
            ItemStack stack, Level world, List<Component> list, TooltipFlag flag, CallbackInfo ci
    ) {
        super.appendHoverText(stack, world, list, flag);
        Object self = this;
        if (self instanceof MalachiteReflectorItem) {
            list.add(Component.translatable("tooltip.enlightened_end.malachite_reflector.line1").withStyle(ChatFormatting.DARK_GRAY));
            list.add(Component.translatable("tooltip.enlightened_end.malachite_reflector.line2").withStyle(ChatFormatting.DARK_GRAY));
            list.add(Component.translatable("tooltip.enlightened_end.malachite_reflector.line3").withStyle(ChatFormatting.DARK_GRAY));
        } else if (self instanceof RootingBladeItem) {
            list.add(Component.translatable("tooltip.enlightened_end.rooting_blade").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            list.add(Component.translatable("tooltip.enlightened_end.wip").withStyle(ChatFormatting.DARK_GRAY));
        }
        ci.cancel();
    }
}
