package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import com.hp.enlightendbugfix.util.EfSafeEntityHelper;
import net.mcreator.enlightened_end.procedures.VoidsteelPickaxeRightclickedProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.TierSortingRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复虚空钢镐（Adamantite Pickaxe）右键远距离挖掘：
 * 1. 移除客户端 Minecraft 类引用，防止专用服务端崩溃；
 * 2. 修正原本对玩家脚下 (x, y, z) 检测挖掘等级而非对射线命中坐标检测的严重逻辑错位；
 * 3. 增加 getDestroySpeed >= 0 不可破坏方块校验，彻底修复隔空敲碎基岩与末地传送门框架的恶性漏洞。
 */
@Mixin(value = VoidsteelPickaxeRightclickedProcedure.class, remap = false)
public abstract class VoidsteelPickaxeRightclickedMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$safeRemoteMineTargetBlock(
            LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (entity == null || world == null || EfSafeEntityHelper.isAdventure(entity)) {
            ci.cancel();
            return;
        }

        if (!EfBugfixConfig.FIX_VOIDSTEEL_PICKAXE_RAYTRACE.get()) {
            return;
        }

        Vec3 eye = entity.getEyePosition(1.0F);
        Vec3 reach = eye.add(entity.getViewVector(1.0F).scale(9.0D));
        BlockHitResult hit = entity.level().clip(new ClipContext(eye, reach, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
        if (hit.getType() != HitResult.Type.BLOCK) {
            ci.cancel();
            return;
        }

        BlockPos targetPos = hit.getBlockPos();
        BlockState targetState = world.getBlockState(targetPos);
        if (targetState.isAir() || targetState.getDestroySpeed(world, targetPos) < 0.0F) {
            ci.cancel();
            return;
        }

        if (enlightendbugfix$getHarvestLevel(targetState) <= 2 && world instanceof Level level && !level.isClientSide()) {
            ItemStack tool = entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY;
            BlockEntity be = level.getBlockEntity(targetPos);
            Block.dropResources(targetState, level, targetPos, be, entity, tool);
            level.destroyBlock(targetPos, false, entity);
        }

        ci.cancel();
    }

    @Unique
    private static int enlightendbugfix$getHarvestLevel(BlockState state) {
        return TierSortingRegistry.getSortedTiers()
                .stream()
                .filter(t -> t.getTag() != null && state.is(t.getTag()))
                .map(Tier::getLevel)
                .findFirst()
                .orElse(0);
    }
}
