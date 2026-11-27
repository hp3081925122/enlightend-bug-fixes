package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModParticleTypes;
import net.mcreator.enlightened_end.procedures.FreezingTotemClientDisplayRandomTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 修复 MCR-54：将循环条件内动态 Mth.nextInt(RandomSource.create(), 10, 12) 提前到循环外，并一次性解析朝向消除每刻 50+ 次匿名类分配
@Mixin(value = FreezingTotemClientDisplayRandomTickProcedure.class, remap = false)
public abstract class FreezingTotemClientDisplayFixMixin {
    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/level/block/state/BlockState;)V", at = @At("HEAD"), cancellable = true)
    private static void efFix$hoistFrostParticles(LevelAccessor world, double x, double y, double z, BlockState blockstate, CallbackInfo ci) {
        ci.cancel();
        if (world == null || blockstate == null) {
            return;
        }
        Direction dir = Direction.NORTH;
        if (blockstate.getBlock().getStateDefinition().getProperty("facing") instanceof DirectionProperty dp) {
            dir = blockstate.getValue(dp);
        } else if (blockstate.getBlock().getStateDefinition().getProperty("axis") instanceof EnumProperty<?> ep) {
            Object val = blockstate.getValue(ep);
            if (val instanceof Direction.Axis axis) {
                dir = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
            }
        }

        int sx = dir.getStepX();
        int sy = dir.getStepY();
        int sz = dir.getStepZ();
        int found = 0;
        for (int step = 1; step <= 7; step++) {
            if (!world.isEmptyBlock(BlockPos.containing(x + sx * step, y + sy * step, z + sz * step))) {
                break;
            }
            found++;
        }

        RandomSource random = world.getRandom();
        int particleCount = Mth.nextInt(random, 10, 12);
        SimpleParticleType frost = EnlightenedEndModParticleTypes.FROST_PARTICLE.get();
        for (int i = 0; i < particleCount; i++) {
            double px = x + sx * Mth.nextInt(random, 0, found) + random.nextDouble();
            double py = y + sy * Mth.nextInt(random, 0, found) + random.nextDouble();
            double pz = z + sz * Mth.nextInt(random, 0, found) + random.nextDouble();
            world.addParticle(frost, px, py, pz, 0.0, 0.0, 0.0);
        }
    }
}
