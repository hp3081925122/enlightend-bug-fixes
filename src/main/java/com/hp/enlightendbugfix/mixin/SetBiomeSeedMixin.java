package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import com.hp.enlightendbugfix.util.EfBiomeNoiseHelper;
import net.mcreator.enlightened_end.procedures.SetBiomeSeedProcedure;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复实体跨维度传送时 SetBiomeSeedProcedure 改用 LegacyRandomSource 重建噪声导致末地子群系瞬间错位以及客户端空指针崩溃的问题。
 */
@Mixin(value = SetBiomeSeedProcedure.class, remap = false)
public abstract class SetBiomeSeedMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$keepConsistentBiomeNoise(Event event, LevelAccessor world, CallbackInfo ci) {
        if (!EfBugfixConfig.FIX_BIOME_NOISE_SEED.get()) {
            if (world == null || world.isClientSide() || world.getServer() == null) {
                ci.cancel();
            }
            return;
        }
        SetBiomeSeedProcedure.generator = EfBiomeNoiseHelper.getNoise(world);
        ci.cancel();
    }
}
