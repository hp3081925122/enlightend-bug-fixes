package com.hp.enlightendbugfix.mixin;

import com.hp.enlightendbugfix.config.EfBugfixConfig;
import java.util.Optional;
import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.mcreator.enlightened_end.procedures.NuclearFurnaceUpdateTickProcedure;
import net.mcreator.enlightened_end.procedures.RadiationEmissionProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复核熔炉（NuclearFurnaceUpdateTickProcedure）多项核心缺陷：
 * 1. 燃料槽恰好有 2 根铱棒时直接把剩余的第 2 根清空吞掉；
 * 2. 无可烧炼输入或产物槽已满时仍白白消耗铱棒；
 * 3. 放入物品首刻（cooking == 0）0 tick 瞬间烧完第一件物品且忽略配方产物数量与堆叠上限；
 * 4. 空闲状态 fuelTime 无限扣成负数并永久每秒发送 20 次 sendBlockUpdated 网络包。
 */
@Mixin(value = NuclearFurnaceUpdateTickProcedure.class, remap = false)
public abstract class NuclearFurnaceUpdateTickMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$fixedNuclearFurnaceTick(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!EfBugfixConfig.FIX_NUCLEAR_FURNACE.get()) {
            return;
        }
        if (!(world instanceof Level level) || level.isClientSide()) {
            ci.cancel();
            return;
        }

        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            ci.cancel();
            return;
        }

        IItemHandlerModifiable handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null)
                .filter(IItemHandlerModifiable.class::isInstance)
                .map(IItemHandlerModifiable.class::cast)
                .orElse(null);
        if (handler == null || handler.getSlots() < 7) {
            ci.cancel();
            return;
        }

        CompoundTag data = blockEntity.getPersistentData();
        double prevFuel = data.getDouble("fuelTime");
        if (prevFuel < 0.0D) {
            prevFuel = 0.0D;
            data.putDouble("fuelTime", 0.0D);
        }
        boolean wasBurning = prevFuel > 0.0D;

        // 预先检查 1~3 号输入槽是否存在至少一个可烧炼且对应 4~6 号输出槽能放下的配方
        ItemStack[] results = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
        boolean anyCanSmelt = false;
        SimpleContainer tempInput = new SimpleContainer(1);
        for (int i = 0; i < 3; i++) {
            ItemStack input = handler.getStackInSlot(i + 1);
            if (!input.isEmpty()) {
                tempInput.setItem(0, input);
                Optional<SmeltingRecipe> recipeOpt = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, tempInput, level);
                if (recipeOpt.isPresent()) {
                    ItemStack res = recipeOpt.get().getResultItem(level.registryAccess()).copy();
                    if (!res.isEmpty() && enlightendbugfix$canFitOutput(handler.getStackInSlot(i + 4), res)) {
                        results[i] = res;
                        anyCanSmelt = true;
                    }
                }
            }
        }

        double fuelTime = prevFuel;
        boolean dirty = false;

        // 仅在燃料耗尽且确实存在可烧炼输入时消耗 1 根铱棒
        if (fuelTime <= 0.0D && anyCanSmelt) {
            ItemStack fuelStack = handler.getStackInSlot(0).copy();
            if (fuelStack.getItem() == EnlightenedEndModItems.IRRADIUM_BAR.get() && fuelStack.getCount() > 0) {
                fuelStack.shrink(1);
                handler.setStackInSlot(0, fuelStack.isEmpty() ? ItemStack.EMPTY : fuelStack);
                fuelTime = 4200.0D;
                data.putDouble("fuelTime", fuelTime);
                dirty = true;

                if (level instanceof ServerLevel serverLevel) {
                    ItemEntity depleted = new ItemEntity(
                            serverLevel,
                            x + 0.5D,
                            y,
                            z + 0.5D,
                            new ItemStack(EnlightenedEndModItems.DEPLETED_IRRADIUM_BAR.get())
                    );
                    depleted.setPickUpDelay(10);
                    serverLevel.addFreshEntity(depleted);
                }
            }
        }

        // 燃烧期间释放周围辐射（支持间隔配置）
        if (fuelTime > 0.0D) {
            int interval = Math.max(1, EfBugfixConfig.NUCLEAR_FURNACE_RADIATION_INTERVAL.get());
            if (interval <= 1 || level.getGameTime() % interval == 0L) {
                RadiationEmissionProcedure.execute(level, x, y, z);
            }
        }

        // 处理 3 个并行烧炼槽（保持每件物品 100 tick 的烧炼耗时，消除 0 tick 瞬烧 Bug）
        for (int i = 0; i < 3; i++) {
            String key = "cooking" + (double) (i + 1);
            double cooking = data.getDouble(key);
            ItemStack result = results[i];

            if (fuelTime > 0.0D && !result.isEmpty()) {
                if (cooking <= 0.0D) {
                    // 开始新一轮 100 tick 烧炼计时
                    data.putDouble(key, 100.0D);
                    dirty = true;
                } else {
                    cooking -= 1.0D;
                    if (cooking <= 0.0D) {
                        // 满 100 tick 产出烧炼结果
                        ItemStack outSlot = handler.getStackInSlot(i + 4).copy();
                        if (outSlot.isEmpty()) {
                            handler.setStackInSlot(i + 4, result.copy());
                        } else {
                            outSlot.grow(result.getCount());
                            handler.setStackInSlot(i + 4, outSlot);
                        }

                        ItemStack inSlot = handler.getStackInSlot(i + 1).copy();
                        inSlot.shrink(1);
                        handler.setStackInSlot(i + 1, inSlot.isEmpty() ? ItemStack.EMPTY : inSlot);

                        // 若仍有同类输入且输出未满，无缝衔接到下一件 100 tick，否则归零
                        ItemStack remainingIn = handler.getStackInSlot(i + 1);
                        ItemStack updatedOut = handler.getStackInSlot(i + 4);
                        if (!remainingIn.isEmpty() && enlightendbugfix$canFitOutput(updatedOut, result)) {
                            data.putDouble(key, 100.0D);
                        } else {
                            data.putDouble(key, 0.0D);
                        }
                        dirty = true;
                    } else {
                        data.putDouble(key, cooking);
                        dirty = true;
                    }
                }
            } else if (cooking != 0.0D) {
                // 输入被取走或燃料耗尽时重置当前槽位进度
                data.putDouble(key, 0.0D);
                dirty = true;
            }
        }

        if (fuelTime > 0.0D) {
            fuelTime = Math.max(0.0D, fuelTime - 1.0D);
            data.putDouble("fuelTime", fuelTime);
            dirty = true;
        }

        if (dirty) {
            blockEntity.setChanged();
        }

        // 客户端 GUI 仅依赖 fuelTime > 0 显示辐射工作图标，只在点火与熄火状态跳变时发送方块实体同步包
        boolean nowBurning = fuelTime > 0.0D;
        if (wasBurning != nowBurning) {
            BlockState state = level.getBlockState(pos);
            level.sendBlockUpdated(pos, state, state, 3);
        }

        ci.cancel();
    }

    @Unique
    private static boolean enlightendbugfix$canFitOutput(ItemStack existingOut, ItemStack recipeResult) {
        if (existingOut.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameTags(existingOut, recipeResult)) {
            return false;
        }
        int maxStack = Math.min(64, existingOut.getMaxStackSize());
        return existingOut.getCount() + recipeResult.getCount() <= maxStack;
    }
}
