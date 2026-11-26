package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.procedures.SacrificalStatueOnBlockRightClickedProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 将献祭雕像（Offering Statue）交互时发送给玩家的硬编码英文动作栏消息改为标准翻译键，
 * 并优先读取雕像 0 号槽位物品的本地化名称 Component，确保祭品名称自动适配玩家语言。
 */
@Mixin(value = SacrificalStatueOnBlockRightClickedProcedure.class, remap = false)
public abstract class SacrificalStatueLocalizationMixin {
    @Redirect(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V",
                    remap = true
            )
    )
    private static void enlightendbugfix$localizeStatueActionbarMessage(
            Player player,
            Component originalMessage,
            boolean actionBar,
            Event event,
            LevelAccessor world,
            double x,
            double y,
            double z,
            Entity entity
    ) {
        String raw = originalMessage.getString();
        if (raw.contains("accepts your offering")) {
            player.displayClientMessage(
                    Component.translatable("message.enlightened_end.offering_statue.accepts"),
                    actionBar
            );
            return;
        }
        if (raw.contains("The Statue requires a ")) {
            Component itemName = null;
            BlockEntity be = world.getBlockEntity(BlockPos.containing(x, y, z));
            if (be != null) {
                ItemStack required = be.getCapability(ForgeCapabilities.ITEM_HANDLER, null)
                        .map(handler -> handler.getStackInSlot(0))
                        .orElse(ItemStack.EMPTY);
                if (!required.isEmpty()) {
                    itemName = required.getHoverName();
                } else {
                    String fallback = be.getPersistentData().getString("itemRequired");
                    if (!fallback.isEmpty()) {
                        itemName = Component.literal(fallback);
                    }
                }
            }
            if (itemName != null) {
                player.displayClientMessage(
                        Component.translatable("message.enlightened_end.offering_statue.requires", itemName),
                        actionBar
                );
                return;
            }
        }
        player.displayClientMessage(originalMessage, actionBar);
    }
}
