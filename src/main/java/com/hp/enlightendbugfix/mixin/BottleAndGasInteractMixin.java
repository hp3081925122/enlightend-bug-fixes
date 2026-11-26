package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.entity.BrainfeederEntity;
import net.mcreator.enlightened_end.entity.DisturbedHeliumGasEntity;
import net.mcreator.enlightened_end.entity.DisturbedXenonGasEntity;
import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.mcreator.enlightened_end.procedures.BottleEnsnareProcedure;
import net.mcreator.enlightened_end.procedures.CollectDistrubedHeliumGasProcedure;
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
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 BottleEnsnareProcedure 与 CollectDistrubedHeliumGasProcedure 将创造模式检查或进度发放目标错写成被右键生物 entity，
 * 导致 milk_ensnare 进度永远无法达成及创造模式下仍扣除玩家瓶子/气罐的问题。
 */
@Mixin(value = {
        BottleEnsnareProcedure.class,
        CollectDistrubedHeliumGasProcedure.class
}, remap = false)
public abstract class BottleAndGasInteractMixin {
    @Unique
    private static final ResourceLocation SOUND_BOTTLE_FILL = new ResourceLocation("item.bottle.fill");
    @Unique
    private static final ResourceLocation SOUND_HELIUM = new ResourceLocation("enlightened_end:helium_place");
    @Unique
    private static final ResourceLocation SOUND_XENON = new ResourceLocation("enlightened_end:xenon_place");
    @Unique
    private static final ResourceLocation ADV_MILK_ENSNARE = new ResourceLocation("enlightened_end:milk_ensnare");
    @Unique
    private static final ResourceLocation ADV_CAPTURE_GAS = new ResourceLocation("enlightened_end:capture_gas");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$fixEntitySourceEntitySwap(
            Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, CallbackInfo ci
    ) {
        if (entity == null || !(sourceentity instanceof LivingEntity actor)) {
            ci.cancel();
            return;
        }

        ItemStack mainHand = actor.getMainHandItem();

        if (entity instanceof BrainfeederEntity
                && mainHand.getItem() == Items.GLASS_BOTTLE
                && entity.getPersistentData().getDouble("hunger") == 300.0D) {
            if (!EfSafeEntityHelper.isCreative(sourceentity)) {
                mainHand.shrink(1);
            }
            actor.swing(InteractionHand.MAIN_HAND, true);
            if (sourceentity instanceof Player player) {
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(EnlightenedEndModItems.BUBBLE_JELLY_BOTTLE.get()));
            }
            entity.getPersistentData().putDouble("hunger", 0.0D);
            enlightendbugfix$playSound(world, x, y, z, SOUND_BOTTLE_FILL, SoundSource.PLAYERS);
            EfSafeEntityHelper.awardAdvancement(sourceentity, ADV_MILK_ENSNARE);
        } else if ((entity instanceof DisturbedHeliumGasEntity || entity instanceof DisturbedXenonGasEntity)
                && mainHand.getItem() == EnlightenedEndModItems.GAS_JAR.get()) {
            boolean isHelium = entity instanceof DisturbedHeliumGasEntity;
            actor.swing(InteractionHand.MAIN_HAND, true);
            if (!entity.level().isClientSide()) {
                entity.discard();
            }
            enlightendbugfix$playSound(world, x, y, z, isHelium ? SOUND_HELIUM : SOUND_XENON, SoundSource.BLOCKS);
            if (!EfSafeEntityHelper.isCreative(sourceentity)) {
                mainHand.shrink(1);
            }
            if (sourceentity instanceof Player player) {
                ItemStack filled = new ItemStack(isHelium ? EnlightenedEndModItems.HELIUM_JAR.get() : EnlightenedEndModItems.XENON_JAR.get());
                ItemHandlerHelper.giveItemToPlayer(player, filled);
            }
            EfSafeEntityHelper.awardAdvancement(sourceentity, ADV_CAPTURE_GAS);
        }

        ci.cancel();
    }

    @Unique
    private static void enlightendbugfix$playSound(LevelAccessor world, double x, double y, double z, ResourceLocation soundId, SoundSource source) {
        if (world instanceof Level level) {
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(soundId);
            if (sound == null) {
                return;
            }
            if (!level.isClientSide()) {
                level.playSound(null, BlockPos.containing(x, y, z), sound, source, 1.0F, 1.0F);
            } else {
                level.playLocalSound(x, y, z, sound, source, 1.0F, 1.0F, false);
            }
        }
    }
}
