package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.RadioactiveItemsProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 RadioactiveItemsProcedure 每 tick 克隆全部 36 格物品、动态创建百余次 TagKey 以及单 tick 发送多达 72 次同步包的问题。
 */
@Mixin(value = RadioactiveItemsProcedure.class, remap = false)
public abstract class RadioactiveItemsMixin {
    @Unique
    private static final TagKey<Item> TAG_LOW = ItemTags.create(new ResourceLocation("enlightened_end:low_radioactive"));
    @Unique
    private static final TagKey<Item> TAG_MEDIUM = ItemTags.create(new ResourceLocation("enlightened_end:medium_radioactive"));
    @Unique
    private static final TagKey<Item> TAG_HIGH = ItemTags.create(new ResourceLocation("enlightened_end:high_radioactive"));
    @Unique
    private static final TagKey<Item> TAG_EXTREME = ItemTags.create(new ResourceLocation("enlightened_end:extreme_radioactive"));

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$batchRadioactiveInventoryScan(Event event, LevelAccessor world, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof Player player) || world == null || world.isClientSide() || EfSafeEntityHelper.isCreative(player) || EfSafeEntityHelper.isSpectator(player)) {
            ci.cancel();
            return;
        }

        EnlightenedEndModVariables.PlayerVariables vars = EfSafeEntityHelper.getMutablePlayerVars(player);
        if (vars == null) {
            ci.cancel();
            return;
        }

        Inventory inv = player.getInventory();
        int totalSlots = inv.getContainerSize();
        int addedRads = 0;
        boolean foundAny = false;

        for (int i = 0; i < totalSlots; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            int stacksOfFour = stack.getCount() / 4;
            if (stack.is(TAG_LOW)) {
                addedRads += 3 * stacksOfFour;
                foundAny = true;
            } else if (stack.is(TAG_MEDIUM)) {
                addedRads += 6 * stacksOfFour;
                foundAny = true;
            } else if (stack.is(TAG_HIGH)) {
                addedRads += 9 * stacksOfFour;
                foundAny = true;
            } else if (stack.is(TAG_EXTREME)) {
                addedRads += 12 * stacksOfFour;
                foundAny = true;
            }
        }

        if (foundAny) {
            boolean wasUnradiated = vars.radiated <= 0.0D;
            int prevBar = (int) (vars.radiation / 650.0D);
            vars.radiation = Math.min(13650.0D, vars.radiation + (double) addedRads);
            vars.radiated = 40.0D;
            int nextBar = (int) (vars.radiation / 650.0D);
            if (wasUnradiated || prevBar != nextBar || player.tickCount % 10 == 0) {
                vars.syncPlayerVariables(player);
            }
        }

        ci.cancel();
    }
}
