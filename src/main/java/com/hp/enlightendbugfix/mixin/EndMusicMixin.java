package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.EndMusicProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
 * 修复 EndMusicProcedure 执行 stopsound @a 强行中断全服所有玩家音乐以及每 tick 同步 music_time 的发包风暴。
 */
@Mixin(value = EndMusicProcedure.class, remap = false)
public abstract class EndMusicMixin {
    @Unique
    private static final ResourceLocation BIOME_BARRENS = new ResourceLocation("end_barrens");
    @Unique
    private static final ResourceLocation BIOME_HIGHLANDS = new ResourceLocation("end_highlands");
    @Unique
    private static final ResourceLocation BIOME_MIDLANDS = new ResourceLocation("end_midlands");
    @Unique
    private static final ResourceLocation BIOME_SMALL_ISLANDS = new ResourceLocation("small_end_islands");
    @Unique
    private static final ResourceLocation VANILLA_END_MUSIC = new ResourceLocation("minecraft", "music.end");
    @Unique
    private static final ResourceLocation MUSIC_SEER = new ResourceLocation("enlightened_end:music.seer");
    @Unique
    private static final ResourceLocation MUSIC_REGRET = new ResourceLocation("enlightened_end:music.regret");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$perPlayerEndMusicWithoutGlobalStopsound(
            Event event, LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (!EfBugfixConfig.FIX_GLOBAL_MUSIC_STOPSOUND.get() && !EfBugfixConfig.OPTIMIZE_PACKET_STORMS.get()) {
            return;
        }
        if (entity == null || !(world instanceof Level level) || level.dimension() != Level.END) {
            ci.cancel();
            return;
        }

        EnlightenedEndModVariables.PlayerVariables vars = EfSafeEntityHelper.getMutablePlayerVars(entity);
        if (vars == null) {
            ci.cancel();
            return;
        }

        Holder<Biome> biome = level.getBiome(BlockPos.containing(x, y, z));
        if (!biome.is(BIOME_BARRENS) && !biome.is(BIOME_HIGHLANDS) && !biome.is(BIOME_MIDLANDS) && !biome.is(BIOME_SMALL_ISLANDS)) {
            ci.cancel();
            return;
        }

        if (!level.isClientSide() && entity instanceof ServerPlayer serverPlayer) {
            if (vars.music_time <= 0.0D) {
                RandomSource random = level.getRandom();
                boolean playSeer = random.nextBoolean();
                vars.music_time = (double) Mth.nextInt(random, 8000, 24000);

                // 仅停止当前玩家自己的原版末地音乐并向该玩家单独下发末地曲目，绝不影响全服其他玩家
                serverPlayer.connection.send(new ClientboundStopSoundPacket(VANILLA_END_MUSIC, SoundSource.MUSIC));
                SoundEvent chosen = ForgeRegistries.SOUND_EVENTS.getValue(playSeer ? MUSIC_SEER : MUSIC_REGRET);
                if (chosen != null) {
                    serverPlayer.connection.send(new ClientboundSoundPacket(
                            ForgeRegistries.SOUND_EVENTS.getHolder(chosen).orElseThrow(),
                            SoundSource.MUSIC,
                            x, y, z,
                            100.0F, 1.0F,
                            random.nextLong()
                    ));
                }
            } else {
                vars.music_time -= 1.0D;
            }
        }
        ci.cancel();
    }
}
