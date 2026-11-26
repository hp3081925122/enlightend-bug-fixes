package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.entity.RinglingEntity;
import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.mcreator.enlightened_end.procedures.FeedRinglingProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复喂食 Ringling 时将玩家判定错写在目标生物 entity 上导致 feed_ringling_advancement 进度永远无法达成的 Bug。
 */
@Mixin(value = FeedRinglingProcedure.class, remap = false)
public abstract class FeedRinglingMixin {
    @Unique
    private static final ResourceLocation STRIDER_EAT_SOUND = new ResourceLocation("entity.strider.eat");
    @Unique
    private static final ResourceLocation ADV_FEED_RINGLING = new ResourceLocation("enlightened_end:feed_ringling_advancement");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$awardFeedRinglingToPlayer(
            Event event, LevelAccessor world, Entity entity, Entity sourceentity, CallbackInfo ci
    ) {
        if (!(entity instanceof RinglingEntity ringling) || !(sourceentity instanceof LivingEntity feeder)) {
            ci.cancel();
            return;
        }

        ItemStack mainHand = feeder.getMainHandItem();
        if (mainHand.getItem() == EnlightenedEndModItems.SQUISHED_BOUNCER.get() && !ringling.getPersistentData().getBoolean("full")) {
            if (world instanceof Level level) {
                SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(STRIDER_EAT_SOUND);
                if (sound != null) {
                    if (!level.isClientSide()) {
                        level.playSound(null, BlockPos.containing(ringling.getX(), ringling.getY(), ringling.getZ()), sound, SoundSource.PLAYERS, 1.0F, 1.2F);
                    } else {
                        level.playLocalSound(ringling.getX(), ringling.getY(), ringling.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.2F, false);
                    }
                }
            }

            if (!ringling.level().isClientSide()) {
                ringling.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 2));
            }
            ringling.getPersistentData().putBoolean("full", true);
            feeder.swing(InteractionHand.MAIN_HAND, true);

            if (!EfSafeEntityHelper.isCreative(sourceentity)) {
                mainHand.shrink(1);
            }

            EfSafeEntityHelper.awardAdvancement(sourceentity, ADV_FEED_RINGLING);
        }

        ci.cancel();
    }
}
