package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.network.EnlightenedEndModVariables;
import net.mcreator.enlightened_end.procedures.PlayerEnterEndProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复首次进入末地每 tick 分配 PlayerVariables 并通过 /tellraw @p 刷 Patreon 赞助广告的问题。
 */
@Mixin(value = PlayerEnterEndProcedure.class, remap = false)
public abstract class PlayerEnterEndMixin {
    @Unique
    private static final ResourceLocation BIOME_BARRENS = new ResourceLocation("end_barrens");
    @Unique
    private static final ResourceLocation BIOME_MIDLANDS = new ResourceLocation("end_midlands");
    @Unique
    private static final ResourceLocation BIOME_HIGHLANDS = new ResourceLocation("end_highlands");
    @Unique
    private static final ResourceLocation BIOME_SMALL_ISLANDS = new ResourceLocation("small_end_islands");

    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$cleanPlayerEnterEnd(
            Event event, LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (!(entity instanceof ServerPlayer serverPlayer) || !(world instanceof Level level) || level.isClientSide()) {
            ci.cancel();
            return;
        }
        if (level.dimension() != Level.END || !serverPlayer.isAlive()) {
            ci.cancel();
            return;
        }

        EnlightenedEndModVariables.PlayerVariables vars = EfSafeEntityHelper.getMutablePlayerVars(serverPlayer);
        if (vars == null || vars.enteredEnd) {
            ci.cancel();
            return;
        }

        Holder<Biome> biome = level.getBiome(BlockPos.containing(x, y, z));
        if (biome.is(BIOME_BARRENS) || biome.is(BIOME_MIDLANDS) || biome.is(BIOME_HIGHLANDS) || biome.is(BIOME_SMALL_ISLANDS)) {
            if (!EfBugfixConfig.DISABLE_PATREON_CHAT_MESSAGE.get()) {
                serverPlayer.sendSystemMessage(
                        Component.translatable("message.enlightened_end.patreon_notice")
                                .withStyle(style -> style.withColor(ChatFormatting.YELLOW)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.patreon.com/lixir")))
                );
            }
            vars.enteredEnd = true;
            vars.syncPlayerVariables(serverPlayer);
        }
        ci.cancel();
    }
}
