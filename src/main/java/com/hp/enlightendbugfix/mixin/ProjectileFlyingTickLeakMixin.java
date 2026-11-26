package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModParticleTypes;
import net.mcreator.enlightened_end.procedures.BubbleDartWhileProjectileFlyingTickProcedure;
import net.mcreator.enlightened_end.procedures.HeliumDartFlyingProcedure;
import net.mcreator.enlightened_end.procedures.XenonDartFlyingProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复三种吹箭在飞行期间每 tick 向全局 workQueue 注册延迟 400 tick 任务导致的客户端永久内存泄漏与服务端队列膨胀。
 */
@Mixin(value = {
        BubbleDartWhileProjectileFlyingTickProcedure.class,
        HeliumDartFlyingProcedure.class,
        XenonDartFlyingProcedure.class
}, remap = false)
public abstract class ProjectileFlyingTickLeakMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$cleanFlyingTickWithoutWorkQueue(
            LevelAccessor world, double x, double y, double z, Entity immediatesourceentity, CallbackInfo ci
    ) {
        if (immediatesourceentity == null) {
            ci.cancel();
            return;
        }

        immediatesourceentity.setNoGravity(true);
        String procName = immediatesourceentity.getType().toShortString();

        // 根据对应吹箭实体类型保持原有升力/下坠与粒子逻辑
        if (procName.contains("helium")) {
            Vec3 dm = immediatesourceentity.getDeltaMovement();
            immediatesourceentity.setDeltaMovement(dm.x(), dm.y() + 0.02D, dm.z());
            if (immediatesourceentity.level().getRandom().nextInt(10) == 0) {
                world.addParticle(EnlightenedEndModParticleTypes.HELIUM_BUBBLE_POP.get(), x, y, z, 0.0D, 0.05D, 0.0D);
            }
            if (y > 320.0D && !immediatesourceentity.level().isClientSide()) {
                immediatesourceentity.discard();
                ci.cancel();
                return;
            }
        } else if (procName.contains("xenon")) {
            Vec3 dm = immediatesourceentity.getDeltaMovement();
            immediatesourceentity.setDeltaMovement(dm.x(), dm.y() - 0.02D, dm.z());
            if (immediatesourceentity.level().getRandom().nextInt(10) == 0) {
                world.addParticle(EnlightenedEndModParticleTypes.XENON_BUBBLE_POP.get(), x, y, z, 0.0D, -0.05D, 0.0D);
            }
        } else {
            if (immediatesourceentity.level().getRandom().nextInt(20) == 0) {
                world.addParticle(EnlightenedEndModParticleTypes.OOZE_GAS_BUBBLE.get(), x, y, z, 0.0D, 0.05D, 0.0D);
            }
        }

        // 用实体自身存活刻数替代每刻向全局队列塞闭包
        if (!immediatesourceentity.level().isClientSide() && immediatesourceentity.tickCount >= 400) {
            immediatesourceentity.discard();
        }
        ci.cancel();
    }
}
