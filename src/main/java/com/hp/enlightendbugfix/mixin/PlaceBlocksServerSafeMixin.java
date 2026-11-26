package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.mcreator.enlightened_end.procedures.PlaceDoubleCeruleanGrassProcedure;
import net.mcreator.enlightened_end.procedures.PlaceHeliumGasProcedure;
import net.mcreator.enlightened_end.procedures.PlaceXenonGasProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复放置双层草、氦气瓶、氙气瓶三个过程直接引用客户端 Minecraft 类导致专用服务端崩溃，
 * 并修复成组手持氦气瓶/氙气瓶放置气体时整组气体瓶被单个空瓶直接覆盖吞没的恶性丢物品 Bug。
 */
@Mixin(value = {
        PlaceDoubleCeruleanGrassProcedure.class,
        PlaceHeliumGasProcedure.class,
        PlaceXenonGasProcedure.class
}, remap = false)
public abstract class PlaceBlocksServerSafeMixin {
    @Unique
    private static final ResourceLocation SOUND_MOSS = new ResourceLocation("block.moss.place");
    @Unique
    private static final ResourceLocation SOUND_HELIUM = new ResourceLocation("enlightened_end:helium_place");
    @Unique
    private static final ResourceLocation SOUND_XENON = new ResourceLocation("enlightened_end:xenon_place");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/core/Direction;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$serverSafePlaceAndPreserveStack(
            Event event, LevelAccessor world, double x, double y, double z, Direction direction, Entity entity, CallbackInfo ci
    ) {
        if (direction == null || !(entity instanceof LivingEntity living) || world == null || EfSafeEntityHelper.isAdventure(entity)) {
            ci.cancel();
            return;
        }

        ItemStack mainHand = living.getMainHandItem();
        if (mainHand.isEmpty()) {
            ci.cancel();
            return;
        }

        Item held = mainHand.getItem();
        BlockPos clickedPos = BlockPos.containing(x, y, z);

        if (held == EnlightenedEndModItems.DOUBLE_CERULEAN_GRASS.get()) {
            if (direction == Direction.DOWN
                    && world.getBlockState(clickedPos).canOcclude()
                    && world.isEmptyBlock(clickedPos.below())
                    && world.isEmptyBlock(clickedPos.below(2))) {
                living.swing(InteractionHand.MAIN_HAND, true);
                enlightendbugfix$playSound(world, clickedPos.below(), SOUND_MOSS, 1.2F);
                world.setBlock(clickedPos.below(), EnlightenedEndModBlocks.CERULEAN_BUSH_TOP.get().defaultBlockState(), 3);
                world.setBlock(clickedPos.below(2), EnlightenedEndModBlocks.CERULEAN_BUSH_BOTTOM.get().defaultBlockState(), 3);
                if (!EfSafeEntityHelper.isCreative(entity)) {
                    mainHand.shrink(1);
                }
            }
        } else if (held == EnlightenedEndModItems.HELIUM_JAR.get() || held == EnlightenedEndModItems.XENON_JAR.get()) {
            BlockPos placePos = clickedPos.relative(direction);
            if (world.isEmptyBlock(placePos)) {
                boolean isHelium = held == EnlightenedEndModItems.HELIUM_JAR.get();
                Block gasBlock = isHelium ? EnlightenedEndModBlocks.HELIUM_GAS.get() : EnlightenedEndModBlocks.XENON_GAS.get();
                ResourceLocation soundId = isHelium ? SOUND_HELIUM : SOUND_XENON;

                living.swing(InteractionHand.MAIN_HAND, true);
                enlightendbugfix$playSound(world, placePos, soundId, 1.0F);
                world.setBlock(placePos, gasBlock.defaultBlockState(), 3);

                if (!EfSafeEntityHelper.isCreative(entity)) {
                    mainHand.shrink(1);
                    ItemStack emptyJar = new ItemStack(EnlightenedEndModItems.GAS_JAR.get());
                    if (mainHand.isEmpty()) {
                        living.setItemInHand(InteractionHand.MAIN_HAND, emptyJar);
                        if (living instanceof Player player) {
                            player.getInventory().setChanged();
                        }
                    } else if (living instanceof Player player) {
                        ItemHandlerHelper.giveItemToPlayer(player, emptyJar);
                    }
                }
            }
        }

        ci.cancel();
    }

    @Unique
    private static void enlightendbugfix$playSound(LevelAccessor world, BlockPos pos, ResourceLocation soundId, float pitch) {
        if (world instanceof Level level) {
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(soundId);
            if (sound == null) {
                return;
            }
            if (!level.isClientSide()) {
                level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, pitch);
            } else {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), sound, SoundSource.BLOCKS, 1.0F, pitch, false);
            }
        }
    }
}
