package com.hp.enlightendbugfix.mixin.client;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import net.mcreator.enlightened_end.client.model.ModelBoostBoots;
import net.mcreator.enlightened_end.item.BoostBootsItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复推进靴（BoostBootsItem.Boots）在每帧盔甲渲染时重复调用两次 bakeLayer 重建整个三维网格的问题。
 */
@Mixin(value = BoostBootsItem.Boots.class, remap = false)
public abstract class BoostBootsModelCacheMixin {
    @Inject(method = "initializeClient", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$cacheBakedArmorModel(Consumer<IClientItemExtensions> consumer, CallbackInfo ci) {
        consumer.accept(new IClientItemExtensions() {
            private HumanoidModel<?> cachedModel;
            private Object lastEntityModelsRef;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel
            ) {
                Object currentModels = Minecraft.getInstance().getEntityModels();
                if (this.cachedModel == null || this.lastEntityModelsRef != currentModels) {
                    ModelBoostBoots<?> baked = new ModelBoostBoots<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelBoostBoots.LAYER_LOCATION));
                    ModelPart emptyPart = new ModelPart(Collections.emptyList(), Collections.emptyMap());
                    this.cachedModel = new HumanoidModel<>(new ModelPart(
                            Collections.emptyList(),
                            Map.of(
                                    "left_leg", baked.LeftLeg,
                                    "right_leg", baked.RightLeg,
                                    "head", emptyPart,
                                    "hat", emptyPart,
                                    "body", emptyPart,
                                    "right_arm", emptyPart,
                                    "left_arm", emptyPart
                            )
                    ));
                    this.lastEntityModelsRef = currentModels;
                }
                this.cachedModel.crouching = living.isShiftKeyDown();
                this.cachedModel.riding = defaultModel.riding;
                this.cachedModel.young = living.isBaby();
                return this.cachedModel;
            }
        });
        ci.cancel();
    }
}
