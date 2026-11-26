package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.entity.BouncelingEntity;
import net.mcreator.enlightened_end.entity.RinglingEntity;
import net.mcreator.enlightened_end.procedures.RinglingAttackProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 RinglingAttackProcedure 把猎物类型判定写反为 !(entity instanceof BouncelingEntity) 且把饱食标记写到被删实体上，
 * 导致 Ringling 一旦攻击任何非 Bounceling 实体（包括玩家、宠物与 Boss）就会直接调用 entity.discard() 秒删对方的致命 Bug。
 */
@Mixin(value = RinglingAttackProcedure.class, remap = false)
public abstract class RinglingAttackMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$onlyEatBouncelingPrey(
            Event event, Entity entity, Entity immediatesourceentity, CallbackInfo ci
    ) {
        if (immediatesourceentity instanceof RinglingEntity ringling
                && entity instanceof BouncelingEntity
                && !ringling.getPersistentData().getBoolean("full")) {
            if (!entity.level().isClientSide()) {
                entity.discard();
            }
            ringling.getPersistentData().putBoolean("full", true);
        }
        ci.cancel();
    }
}
