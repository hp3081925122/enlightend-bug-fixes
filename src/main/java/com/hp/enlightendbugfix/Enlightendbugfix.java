package com.hp.enlightendbugfix;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import com.mojang.logging.LogUtils;
import net.mcreator.enlightened_end.entity.FlashBombEntity;
import net.mcreator.enlightened_end.init.EnlightenedEndModEntities;
import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * 终末之光（Enlightend）Bug 修复与性能优化附属主类。
 */
@Mod(Enlightendbugfix.MOD_ID)
public final class Enlightendbugfix {
    public static final String MOD_ID = "enlightendbugfix";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Enlightendbugfix(FMLJavaModLoadingContext context) {
        // 注册通用配置文件与公共初始化回调
        context.registerConfig(ModConfig.Type.COMMON, EfBugfixConfig.COMMON_SPEC);
        context.getModEventBus().addListener(this::onCommonSetup);
        LOGGER.info("[EnlightendBugFix] Initialized Enlightend Bug Fixes addon.");
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        // 在主线程安全队列中为闪光弹物品补充原版发射器投射物行为，避免发射器仅吐出掉落物
        event.enqueueWork(() -> DispenserBlock.registerBehavior(
                EnlightenedEndModItems.FLASH_BOMB.get(),
                new AbstractProjectileDispenseBehavior() {
                    @Override
                    protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                        FlashBombEntity bomb = new FlashBombEntity(
                                EnlightenedEndModEntities.FLASH_BOMB.get(),
                                position.x(),
                                position.y(),
                                position.z(),
                                level
                        );
                        bomb.setSilent(true);
                        bomb.setCritArrow(true);
                        bomb.setBaseDamage(0.0D);
                        bomb.setKnockback(0);
                        bomb.pickup = AbstractArrow.Pickup.DISALLOWED;
                        return bomb;
                    }
                }
        ));
    }
}
