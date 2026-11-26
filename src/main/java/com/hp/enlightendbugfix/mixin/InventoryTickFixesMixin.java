package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.mcreator.enlightened_end.procedures.RinglingPotItemInInventoryTickProcedure;
import net.mcreator.enlightened_end.procedures.VoidsteelToolInInventoryTickProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复两个物品背包每刻更新 Bug：
 * 1. VoidsteelToolInInventoryTickProcedure 在工具满耐久（damage == 0）时仍每刻设为 -1，导致空损毁标签污染与物品不断上下抖动；
 * 2. RinglingPotItemInInventoryTickProcedure 误检测玩家实体 NBT 而非物品自身 NBT，导致每刻向物品写入 health 标签。
 */
@Mixin(value = {
        VoidsteelToolInInventoryTickProcedure.class,
        RinglingPotItemInInventoryTickProcedure.class
}, remap = false)
public abstract class InventoryTickFixesMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$fixInventoryTicks(Entity entity, ItemStack itemstack, CallbackInfo ci) {
        if (entity == null || itemstack == null || itemstack.isEmpty()) {
            ci.cancel();
            return;
        }

        if (itemstack.getItem() == EnlightenedEndModItems.RINGLING_POT.get()) {
            CompoundTag tag = itemstack.getTag();
            if (tag == null || !tag.contains("health") || tag.getDouble("health") <= 0.0D) {
                itemstack.getOrCreateTag().putDouble("health", 10.0D);
            }
        } else {
            int damage = itemstack.getDamageValue();
            if (damage > 0 && damage < itemstack.getMaxDamage()) {
                ItemStack mainHand = entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY;
                if (mainHand != itemstack && mainHand.getItem() != itemstack.getItem()) {
                    itemstack.setDamageValue(damage - 1);
                }
            }
        }

        ci.cancel();
    }
}
