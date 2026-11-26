package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import com.hp.enlightendbugfix.util.EfBiomeNoiseHelper;
import net.mcreator.enlightened_end.procedures.IsCongealedForestProcedure;
import net.mcreator.enlightened_end.procedures.IsEnderneathProcedure;
import net.mcreator.enlightened_end.procedures.IsExogasBeltProcedure;
import net.mcreator.enlightened_end.procedures.IsFreezingEndProcedure;
import net.mcreator.enlightened_end.procedures.IsOozeSeepsProcedure;
import net.mcreator.enlightened_end.procedures.IsUpperEndProcedure;
import net.mcreator.enlightened_end.procedures.SetBiomeSeedProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 在全部 6 个末地子群系判定过程执行前确保 SimplexNoise 已按当前世界真实种子就绪，消除早期区块生成空指针与多人联机迷雾错位。
 */
@Mixin(value = {
        IsCongealedForestProcedure.class,
        IsFreezingEndProcedure.class,
        IsOozeSeepsProcedure.class,
        IsExogasBeltProcedure.class,
        IsUpperEndProcedure.class,
        IsEnderneathProcedure.class
}, remap = false)
public abstract class SubBiomeChecksMixin {
    @Inject(method = "execute", at = @At("HEAD"))
    private static void enlightendbugfix$ensureGeneratorInitialized(LevelAccessor world, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (EfBugfixConfig.FIX_BIOME_NOISE_SEED.get() || SetBiomeSeedProcedure.generator == null) {
            SetBiomeSeedProcedure.generator = EfBiomeNoiseHelper.getNoise(world);
        }
    }
}
