package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.procedures.BouncelingNutRangedItemUsedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 BouncelingNutRangedItemUsedProcedure 创造模式判定写反（创造反而扣物品、生存反而不扣）
 * 以及先 shrink(1) 再 addCooldown(itemstack.getItem(), 20) 导致单格消耗时将 Items.AIR 加入冷却的问题。
 */
@Mixin(value = BouncelingNutRangedItemUsedProcedure.class, remap = false)
public abstract class BouncelingNutUsedFixMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$fixCreativeCheckAndCooldownItem(Entity entity, ItemStack itemstack, CallbackInfo ci) {
        if (entity instanceof Player player && itemstack != null && !itemstack.isEmpty()) {
            Item usedItem = itemstack.getItem();
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            player.getCooldowns().addCooldown(usedItem, 20);
        }
        ci.cancel();
    }
}
