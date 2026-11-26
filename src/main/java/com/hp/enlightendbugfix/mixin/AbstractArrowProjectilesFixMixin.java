package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.entity.FlashBombEntity;
import net.mcreator.enlightened_end.entity.HeliumDartEntity;
import net.mcreator.enlightened_end.entity.XenonDartEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 补齐全部 3 种 AbstractArrow 投射物（闪光弹、氦气吹箭、氙气吹箭）的安全兜底：
 * 1. 禁止玩家地面或盾反拾取（Pickup.DISALLOWED）；
 * 2. 防止 doPostHurtEffects 中 setArrowCount(getArrowCount() - 1) 发生负数下溢；
 * 3. 为未接入飞行寿命清理的 FlashBombEntity 补充 400 tick 最大存活上限，防止飞向虚空永久滞留。
 */
@Mixin({
        FlashBombEntity.class,
        HeliumDartEntity.class,
        XenonDartEntity.class
})
public abstract class AbstractArrowProjectilesFixMixin extends AbstractArrow {
    protected AbstractArrowProjectilesFixMixin(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void enlightendbugfix$disallowPickupAndCapLifetime(CallbackInfo ci) {
        if (this.pickup != Pickup.DISALLOWED) {
            this.pickup = Pickup.DISALLOWED;
        }
        if (!this.level().isClientSide() && (Object) this instanceof FlashBombEntity && this.tickCount >= 400) {
            this.discard();
        }
    }

    @Redirect(
            method = "doPostHurtEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setArrowCount(I)V"
            )
    )
    private void enlightendbugfix$clampArrowCountUnderflow(LivingEntity entity, int count) {
        entity.setArrowCount(Math.max(0, count));
    }
}
