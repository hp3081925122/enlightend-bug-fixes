package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import java.util.Map;
import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.procedures.StripCeruleanStalkBlockProcedure;
import net.mcreator.enlightened_end.procedures.StripSeldgeHyphaeProcedure;
import net.mcreator.enlightened_end.procedures.StripSeldgeStemProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复三个原木/菌柄去皮过程引用客户端 Minecraft 类导致专用服务端崩溃，以及写死 instanceof AxeItem 导致模块化斧头无法去皮的问题。
 */
@Mixin(value = {
        StripCeruleanStalkBlockProcedure.class,
        StripSeldgeHyphaeProcedure.class,
        StripSeldgeStemProcedure.class
}, remap = false)
public abstract class StripLogsServerSafeMixin {
    @Unique
    private static final ResourceLocation AXE_STRIP_SOUND = new ResourceLocation("item.axe.strip");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$serverSafeToolActionStrip(
            Event event, LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (!(entity instanceof LivingEntity living) || world == null || EfSafeEntityHelper.isAdventure(entity)) {
            ci.cancel();
            return;
        }

        ItemStack mainHand = living.getMainHandItem();
        ItemStack offHand = living.getOffhandItem();
        InteractionHand stripHand = null;
        ItemStack stripTool = ItemStack.EMPTY;
        if (mainHand.canPerformAction(ToolActions.AXE_STRIP)) {
            stripHand = InteractionHand.MAIN_HAND;
            stripTool = mainHand;
        } else if (offHand.canPerformAction(ToolActions.AXE_STRIP)) {
            stripHand = InteractionHand.OFF_HAND;
            stripTool = offHand;
        }
        if (stripHand == null) {
            ci.cancel();
            return;
        }

        BlockPos pos = BlockPos.containing(x, y, z);
        BlockState oldState = world.getBlockState(pos);
        Block oldBlock = oldState.getBlock();
        Block strippedBlock = null;
        if (oldBlock == EnlightenedEndModBlocks.CERULEAN_LOG.get()) {
            strippedBlock = EnlightenedEndModBlocks.STRIPPED_CERULEAN_LOG.get();
        } else if (oldBlock == EnlightenedEndModBlocks.INDIGO_HYPHAE.get()) {
            strippedBlock = EnlightenedEndModBlocks.STRIPPED_INDIGO_HYPHAE.get();
        } else if (oldBlock == EnlightenedEndModBlocks.INDIGO_STEM.get()) {
            strippedBlock = EnlightenedEndModBlocks.STRIPPED_INDIGO_STEM.get();
        }
        if (strippedBlock == null) {
            ci.cancel();
            return;
        }

        living.swing(stripHand, true);
        if (!EfSafeEntityHelper.isCreative(entity)) {
            EquipmentSlot slot = stripHand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stripTool.hurtAndBreak(1, living, e -> e.broadcastBreakEvent(slot));
        }

        if (world instanceof Level level) {
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(AXE_STRIP_SOUND);
            if (sound != null) {
                if (!level.isClientSide()) {
                    level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
                } else {
                    level.playLocalSound(x, y, z, sound, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }
            }
        }

        BlockState newState = enlightendbugfix$copyCompatibleProperties(oldState, strippedBlock.defaultBlockState());
        BlockEntity oldBe = world.getBlockEntity(pos);
        CompoundTag savedNbt = null;
        if (oldBe != null) {
            savedNbt = oldBe.saveWithFullMetadata();
            oldBe.setRemoved();
        }
        world.setBlock(pos, newState, 3);
        if (savedNbt != null) {
            BlockEntity newBe = world.getBlockEntity(pos);
            if (newBe != null) {
                newBe.load(savedNbt);
            }
        }

        ci.cancel();
    }

    @Unique
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState enlightendbugfix$copyCompatibleProperties(BlockState from, BlockState to) {
        for (Map.Entry<Property<?>, Comparable<?>> entry : from.getValues().entrySet()) {
            Property prop = to.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
            if (prop != null && to.getValues().containsKey(prop)) {
                try {
                    to = (BlockState) to.setValue(prop, (Comparable) entry.getValue());
                } catch (Exception ignored) {
                }
            }
        }
        return to;
    }
}
