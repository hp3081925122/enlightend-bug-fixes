package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.procedures.BouncelingEntityDiesProcedure;
import net.mcreator.enlightened_end.procedures.FloatingElevibloomEntityDiesProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 取消 BouncelingEntityDiesProcedure 与 FloatingElevibloomEntityDiesProcedure 中的硬编码掉落刷物，
 * 统一交由标准 JSON 实体战利品表（entities/bouncer.json 与 entities/floating_elevibloom.json）驱动，
 * 避免重复掉落并支持 JEI/JER 查询与抢夺附魔。
 */
@Mixin(value = {
        BouncelingEntityDiesProcedure.class,
        FloatingElevibloomEntityDiesProcedure.class
}, remap = false)
public abstract class HardcodedEntityDropDisableMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$delegateToEntityLootTable(CallbackInfo ci) {
        ci.cancel();
    }
}
