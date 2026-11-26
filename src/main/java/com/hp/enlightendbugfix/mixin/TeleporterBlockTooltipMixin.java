package com.hp.enlightendbugfix.mixin;

import java.util.List;
import net.mcreator.enlightened_end.block.TeleporterBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 将 TeleporterBlock 硬编码英文 Tooltip 改为标准翻译键。
 */
@Mixin(TeleporterBlock.class)
public abstract class TeleporterBlockTooltipMixin extends Block {
    protected TeleporterBlockTooltipMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$localizedTeleporterTooltip(
            ItemStack stack, BlockGetter world, List<Component> list, TooltipFlag flag, CallbackInfo ci
    ) {
        super.appendHoverText(stack, world, list, flag);
        list.add(Component.translatable("tooltip.enlightened_end.teleporter.line1").withStyle(ChatFormatting.DARK_GRAY));
        list.add(Component.translatable("tooltip.enlightened_end.teleporter.line2").withStyle(ChatFormatting.DARK_GRAY));
        ci.cancel();
    }
}
