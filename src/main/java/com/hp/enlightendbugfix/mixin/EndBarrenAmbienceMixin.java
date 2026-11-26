package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.EndBarrenAmbienceProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 EndBarrenAmbienceProcedure 在全维度每 tick 向客户端发 2~4 个 PlayerVariables 同步包的网络风暴。
 */
@Mixin(value = EndBarrenAmbienceProcedure.class, remap = false)
public abstract class EndBarrenAmbienceMixin {
    @Unique
    private static final ResourceLocation BIOME_BARRENS = new ResourceLocation("end_barrens");
    @Unique
    private static final ResourceLocation BIOME_HIGHLANDS = new ResourceLocation("end_highlands");
    @Unique
    private static final ResourceLocation BIOME_MIDLANDS = new ResourceLocation("end_midlands");
    @Unique
    private static final ResourceLocation SOUND_WIND = new ResourceLocation("enlightened_end:endbarrens_wind");
    @Unique
    private static final ResourceLocation SOUND_LOOP = new ResourceLocation("enlightened_end:endbarrens_loop");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$clientOnlyAmbienceTimers(
            Event event, LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (!EfBugfixConfig.OPTIMIZE_PACKET_STORMS.get()) {
            return;
        }
        // 环境风声仅在客户端本地播放，服务端无需每刻递减并同步两个计时器
        if (entity == null || !(world instanceof Level level) || !level.isClientSide()) {
            ci.cancel();
            return;
        }

        EnlightenedEndModVariables.PlayerVariables vars = EfSafeEntityHelper.getMutablePlayerVars(entity);
        if (vars == null) {
            ci.cancel();
            return;
        }

        if (level.dimension() == Level.END) {
            Holder<Biome> biome = level.getBiome(BlockPos.containing(x, y, z));
            if (biome.is(BIOME_BARRENS) || biome.is(BIOME_HIGHLANDS) || biome.is(BIOME_MIDLANDS)) {
                if (vars.endbarrenswind <= 0.0D) {
                    SoundEvent wind = ForgeRegistries.SOUND_EVENTS.getValue(SOUND_WIND);
                    if (wind != null) {
                        level.playLocalSound(x, y, z, wind, SoundSource.AMBIENT, 25.0F, 1.0F, false);
                    }
                }
                if (vars.barrenambiencetimer <= 0.0D) {
                    SoundEvent loop = ForgeRegistries.SOUND_EVENTS.getValue(SOUND_LOOP);
                    if (loop != null) {
                        level.playLocalSound(x, y, z, loop, SoundSource.AMBIENT, 25.0F, 1.0F, false);
                    }
                }
            }
        }

        RandomSource random = level.getRandom();
        if (vars.endbarrenswind <= 0.0D) {
            vars.endbarrenswind = (double) (660 + Mth.nextInt(random, 0, 3000));
        } else {
            vars.endbarrenswind -= 1.0D;
        }

        if (vars.barrenambiencetimer <= 0.0D) {
            vars.barrenambiencetimer = (double) (1240 + Mth.nextInt(random, 0, 400));
        } else {
            vars.barrenambiencetimer -= 1.0D;
        }

        ci.cancel();
    }
}
