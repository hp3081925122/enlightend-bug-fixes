package com.hp.enlightendbugfix.util;

import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

/**
 * 双端安全的实体、游戏模式、进度与玩家变量读取辅助类。
 */
public final class EfSafeEntityHelper {
    // 静态只读兜底变量实例，彻底消除每刻重复创建临时对象的垃圾分配
    private static final EnlightenedEndModVariables.PlayerVariables EMPTY_PLAYER_VARS = new EnlightenedEndModVariables.PlayerVariables();

    private EfSafeEntityHelper() {
    }

    /**
     * 无分配获取玩家的变量能力实例，不存在时返回 null。
     */
    public static EnlightenedEndModVariables.PlayerVariables getMutablePlayerVars(Entity entity) {
        if (entity == null) {
            return null;
        }
        return entity.getCapability(EnlightenedEndModVariables.PLAYER_VARIABLES_CAPABILITY, null).resolve().orElse(null);
    }

    /**
     * 无分配读取玩家变量，不存在时返回静态零值只读兜底对象。
     */
    public static EnlightenedEndModVariables.PlayerVariables getReadOnlyPlayerVars(Entity entity) {
        EnlightenedEndModVariables.PlayerVariables vars = getMutablePlayerVars(entity);
        return vars != null ? vars : EMPTY_PLAYER_VARS;
    }

    /**
     * 双端安全判定实体是否处于指定游戏模式，不在服务端加载任何客户端类。
     */
    public static boolean checkGamemode(Entity entity, GameType targetMode) {
        if (entity instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.getGameModeForPlayer() == targetMode;
        }
        if (entity instanceof Player player && entity.level().isClientSide()) {
            return EfClientGamemodeHelper.isClientPlayerMode(player, targetMode);
        }
        return false;
    }

    public static boolean isAdventure(Entity entity) {
        return checkGamemode(entity, GameType.ADVENTURE);
    }

    public static boolean isSpectator(Entity entity) {
        return checkGamemode(entity, GameType.SPECTATOR);
    }

    public static boolean isCreative(Entity entity) {
        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return true;
        }
        return checkGamemode(entity, GameType.CREATIVE);
    }

    /**
     * 向服务端玩家安全授予指定进度。
     */
    public static void awardAdvancement(Entity entity, ResourceLocation advancementId) {
        if (!(entity instanceof ServerPlayer serverPlayer) || !(serverPlayer.level() instanceof ServerLevel)) {
            return;
        }
        Advancement adv = serverPlayer.server.getAdvancements().getAdvancement(advancementId);
        if (adv == null) {
            return;
        }
        AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(adv);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                serverPlayer.getAdvancements().award(adv, criterion);
            }
        }
    }
}
