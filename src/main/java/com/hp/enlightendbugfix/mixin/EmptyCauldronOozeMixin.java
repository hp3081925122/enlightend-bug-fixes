package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.mcreator.enlightened_end.procedures.EmptyCauldronOozeProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 EmptyCauldronOozeProcedure 腐泥炼药锅交互的两大 Bug：
 * 1. 用空铁桶舀满锅腐泥时返还的仍是空铁桶（Items.BUCKET），而非腐泥桶（OOZE_FLUID_BUCKET）；
 * 2. 装瓶分支误将手持条件写成 OOZE_BOTTLE 而非空玻璃瓶（Items.GLASS_BOTTLE），导致无法用玻璃瓶从炼药锅中舀出腐泥瓶。
 */
@Mixin(value = EmptyCauldronOozeProcedure.class, remap = false)
public abstract class EmptyCauldronOozeMixin {
    @Unique
    private static final ResourceLocation SOUND_BUCKET_FILL = new ResourceLocation("item.bucket.fill");
    @Unique
    private static final ResourceLocation SOUND_OOZE_BOTTLE_FILLED = new ResourceLocation("enlightened_end:ooze_bottle_filled");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$fixOozeCauldronScooping(
            Event event, LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (!(entity instanceof LivingEntity living) || world == null) {
            ci.cancel();
            return;
        }

        BlockPos pos = BlockPos.containing(x, y, z);
        Block block = world.getBlockState(pos).getBlock();
        ItemStack mainHand = living.getMainHandItem();

        if (mainHand.getItem() == Items.BUCKET && block == EnlightenedEndModBlocks.OOZE_CAULDRON_FULL.get()) {
            world.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
            living.swing(InteractionHand.MAIN_HAND, true);
            enlightendbugfix$playSound(world, pos, x, y, z, SOUND_BUCKET_FILL);

            if (!EfSafeEntityHelper.isCreative(entity)) {
                mainHand.shrink(1);
                if (entity instanceof Player player) {
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(EnlightenedEndModItems.OOZE_FLUID_BUCKET.get()));
                }
            } else if (entity instanceof Player player && !player.getInventory().contains(new ItemStack(EnlightenedEndModItems.OOZE_FLUID_BUCKET.get()))) {
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(EnlightenedEndModItems.OOZE_FLUID_BUCKET.get()));
            }

            if (event != null && event.isCancelable()) {
                event.setCanceled(true);
            }
        } else if (mainHand.getItem() == Items.GLASS_BOTTLE
                && (block == EnlightenedEndModBlocks.OOZE_CAULDRON_FULL.get()
                || block == EnlightenedEndModBlocks.OOZE_CAULDRON_2.get()
                || block == EnlightenedEndModBlocks.OOZE_CAULDRON_1.get())) {
            Block nextCauldron;
            if (block == EnlightenedEndModBlocks.OOZE_CAULDRON_FULL.get()) {
                nextCauldron = EnlightenedEndModBlocks.OOZE_CAULDRON_2.get();
            } else if (block == EnlightenedEndModBlocks.OOZE_CAULDRON_2.get()) {
                nextCauldron = EnlightenedEndModBlocks.OOZE_CAULDRON_1.get();
            } else {
                nextCauldron = Blocks.CAULDRON;
            }

            world.setBlock(pos, nextCauldron.defaultBlockState(), 3);
            living.swing(InteractionHand.MAIN_HAND, true);
            enlightendbugfix$playSound(world, pos, x, y, z, SOUND_OOZE_BOTTLE_FILLED);

            if (!EfSafeEntityHelper.isCreative(entity)) {
                mainHand.shrink(1);
                if (entity instanceof Player player) {
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(EnlightenedEndModItems.OOZE_BOTTLE.get()));
                }
            } else if (entity instanceof Player player && !player.getInventory().contains(new ItemStack(EnlightenedEndModItems.OOZE_BOTTLE.get()))) {
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(EnlightenedEndModItems.OOZE_BOTTLE.get()));
            }

            if (event != null && event.isCancelable()) {
                event.setCanceled(true);
            }
        }

        ci.cancel();
    }

    @Unique
    private static void enlightendbugfix$playSound(LevelAccessor world, BlockPos pos, double x, double y, double z, ResourceLocation soundId) {
        if (world instanceof Level level) {
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(soundId);
            if (sound == null) {
                return;
            }
            if (!level.isClientSide()) {
                level.playSound(null, pos, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else {
                level.playLocalSound(x, y, z, sound, SoundSource.PLAYERS, 1.0F, 1.0F, false);
            }
        }
    }
}
