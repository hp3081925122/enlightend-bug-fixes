package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.world.inventory.NuclearFurnaceScreenMenu;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复核熔炉菜单未成功绑定方块实体时 stillValid 恒返回 true 的容器校验漏洞。
 */
@Mixin(NuclearFurnaceScreenMenu.class)
public abstract class NuclearFurnaceScreenMenuMixin {
    @Shadow(remap = false)
    private boolean bound;

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$requireBoundTarget(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!this.bound) {
            cir.setReturnValue(false);
        }
    }
}
