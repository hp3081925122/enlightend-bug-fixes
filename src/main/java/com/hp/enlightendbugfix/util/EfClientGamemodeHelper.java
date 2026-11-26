package com.hp.enlightendbugfix.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

/**
 * 仅在物理客户端侧按需加载的游戏模式查询辅助类，避免专用服务端触发客户端类加载崩溃。
 */
public final class EfClientGamemodeHelper {
    private EfClientGamemodeHelper() {
    }

    public static boolean isClientPlayerMode(Player player, GameType targetMode) {
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener connection = mc.getConnection();
        if (connection == null) {
            return false;
        }
        PlayerInfo info = connection.getPlayerInfo(player.getGameProfile().getId());
        return info != null && info.getGameMode() == targetMode;
    }
}
