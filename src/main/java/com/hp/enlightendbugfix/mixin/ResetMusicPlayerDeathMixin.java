package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.procedures.ResetMusicPlayerDeathProcedure;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复玩家死亡重置背景音乐过程拼接 entity.getDisplayName().getString() 执行 stopsound 命令导致
 * 带空格/颜色码/前缀的昵称失效及命令解析器冗余开销的问题，改为直接向目标 ServerPlayer 发送原生停止音效数据包。
 */
@Mixin(value = ResetMusicPlayerDeathProcedure.class, remap = false)
public abstract class ResetMusicPlayerDeathMixin {
    @Unique
    private static final ResourceLocation MUSIC_SEER = new ResourceLocation("enlightened_end:music.seer");
    @Unique
    private static final ResourceLocation MUSIC_REGRET = new ResourceLocation("enlightened_end:music.regret");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$sendDirectStopSoundPackets(
            Event event, LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (entity instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
            serverPlayer.connection.send(new ClientboundStopSoundPacket(MUSIC_SEER, SoundSource.MUSIC));
            serverPlayer.connection.send(new ClientboundStopSoundPacket(MUSIC_REGRET, SoundSource.MUSIC));
        }
        ci.cancel();
    }
}
