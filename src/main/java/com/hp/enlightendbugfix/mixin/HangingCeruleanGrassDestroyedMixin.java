package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.procedures.HangingCeruleanGrassBlockDestroyedByPlayerProcedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.ToolActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 HangingCeruleanGrassBlockDestroyedByPlayerProcedure 引用客户端 Minecraft 类及硬编码原版剪刀的问题。
 */
@Mixin(value = HangingCeruleanGrassBlockDestroyedByPlayerProcedure.class, remap = false)
public abstract class HangingCeruleanGrassDestroyedMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$serverSafeShearsDrop(
            LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (entity instanceof LivingEntity living && !EfSafeEntityHelper.isAdventure(entity)) {
            ItemStack mainHand = living.getMainHandItem();
            if (mainHand.canPerformAction(ToolActions.SHEARS_DIG)) {
                if (!EfSafeEntityHelper.isCreative(entity)) {
                    mainHand.hurtAndBreak(1, living, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
                if (world instanceof ServerLevel serverLevel) {
                    ItemEntity itemEntity = new ItemEntity(
                            serverLevel,
                            x + 0.5D, y, z + 0.5D,
                            new ItemStack(EnlightenedEndModBlocks.CERULEAN_GRASS.get())
                    );
                    itemEntity.setPickUpDelay(10);
                    serverLevel.addFreshEntity(itemEntity);
                }
            }
        }
        ci.cancel();
    }
}
