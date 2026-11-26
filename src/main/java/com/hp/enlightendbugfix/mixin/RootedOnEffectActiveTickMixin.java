package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.procedures.RootedOnEffectActiveTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 RootedOnEffectActiveTickProcedure 引用客户端 Minecraft 类导致专用服务端崩溃的问题。
 */
@Mixin(value = RootedOnEffectActiveTickProcedure.class, remap = false)
public abstract class RootedOnEffectActiveTickMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$serverSafeRootedTick(
            LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (entity != null && world != null && !EfSafeEntityHelper.isSpectator(entity)) {
            if (world.getBlockState(BlockPos.containing(x, y - 1.0D, z)).canOcclude()
                    && world.isEmptyBlock(BlockPos.containing(x, y, z))) {
                entity.teleportTo(x, y - 1.0D, z);
                if (entity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.teleport(x, y - 1.0D, z, entity.getYRot(), entity.getXRot());
                }
            }
        }
        ci.cancel();
    }
}
