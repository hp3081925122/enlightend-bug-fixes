package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.init.EnlightenedEndModParticleTypes;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.OozeRainParticlesProcedure;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 OozeRainParticlesProcedure 每 tick 发送 3~6 个 PlayerVariables 同步包及每刻创建上千个 RandomSource 的性能开销。
 */
@Mixin(value = OozeRainParticlesProcedure.class, remap = false)
public abstract class OozeRainParticlesMixin {
    @Unique
    private static final ResourceLocation SOUND_RAIN = new ResourceLocation("enlightened_end:ooze_rain");
    @Unique
    private static final ResourceLocation SOUND_RAIN_DISTANT = new ResourceLocation("enlightened_end:ooze_rain_distant");
    @Unique
    private static final ResourceLocation SOUND_RAIN_POP = new ResourceLocation("enlightened_end:ooze_rain_pophigh");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$clientOnlyOozeRainEffects(
            Event event, LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (!EfBugfixConfig.OPTIMIZE_PACKET_STORMS.get()) {
            return;
        }
        // 雨滴粒子与本地音效只在客户端末地且处于腐泥雨期间执行，服务端零发包
        if (entity == null || !(world instanceof Level level) || !level.isClientSide() || level.dimension() != Level.END) {
            ci.cancel();
            return;
        }
        if (!EnlightenedEndModVariables.MapVariables.get(level).oozerain) {
            ci.cancel();
            return;
        }

        EnlightenedEndModVariables.PlayerVariables vars = EfSafeEntityHelper.getMutablePlayerVars(entity);
        if (vars == null) {
            ci.cancel();
            return;
        }

        RandomSource random = level.getRandom();
        int rainCount = EfBugfixConfig.OOZE_RAIN_PARTICLES_PER_TICK.get();
        SimpleParticleType oozeRainParticle = EnlightenedEndModParticleTypes.OOZE_RAIN.get();
        for (int i = 0; i < rainCount; i++) {
            level.addParticle(
                    oozeRainParticle,
                    x + Mth.nextDouble(random, -128.0D, 128.0D),
                    y + Mth.nextDouble(random, -64.0D, 0.0D),
                    z + Mth.nextDouble(random, -128.0D, 128.0D),
                    0.0D, 0.0D, 0.0D
            );
        }

        int dustCount = Math.max(0, (rainCount * 25) / 400);
        SimpleParticleType endDustParticle = EnlightenedEndModParticleTypes.END_DUST.get();
        for (int i = 0; i < dustCount; i++) {
            level.addParticle(
                    endDustParticle,
                    x + Mth.nextDouble(random, -64.0D, 64.0D),
                    y + Mth.nextDouble(random, 0.0D, 64.0D),
                    z + Mth.nextDouble(random, -64.0D, 64.0D),
                    0.0D, 0.0D, 0.0D
            );
        }

        if (vars.raintimer <= 0.0D) {
            double sx = random.nextBoolean() ? 1.0D : -1.0D;
            double sz = random.nextBoolean() ? 1.0D : -1.0D;
            SoundEvent s = ForgeRegistries.SOUND_EVENTS.getValue(SOUND_RAIN);
            if (s != null) {
                level.playLocalSound(x + 16.0D * sx, y, z + 16.0D * sz, s, SoundSource.AMBIENT, 100.0F, 1.0F, false);
            }
            vars.raintimer = 20.0D;
        } else {
            vars.raintimer -= 1.0D;
        }

        if (vars.oozeraindistant <= 0.0D) {
            double sx = random.nextBoolean() ? 1.0D : -1.0D;
            double sz = random.nextBoolean() ? 1.0D : -1.0D;
            SoundEvent s = ForgeRegistries.SOUND_EVENTS.getValue(SOUND_RAIN_DISTANT);
            if (s != null) {
                level.playLocalSound(x + 64.0D * sx, y, z + 64.0D * sz, s, SoundSource.AMBIENT, 100.0F, 1.0F, false);
            }
            vars.oozeraindistant = (double) Mth.nextInt(random, 40, 200);
        } else {
            vars.oozeraindistant -= 1.0D;
        }

        if (vars.oozerainhigh <= 0.0D) {
            double sx = random.nextBoolean() ? 1.0D : -1.0D;
            double sz = random.nextBoolean() ? 1.0D : -1.0D;
            SoundEvent s = ForgeRegistries.SOUND_EVENTS.getValue(SOUND_RAIN_POP);
            if (s != null) {
                level.playLocalSound(x + 64.0D * sx, y, z + 64.0D * sz, s, SoundSource.AMBIENT, 100.0F, 1.0F, false);
            }
            vars.oozerainhigh = (double) Mth.nextInt(random, 40, 200);
        } else {
            vars.oozerainhigh -= 1.0D;
        }

        ci.cancel();
    }
}
