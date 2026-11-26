package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复 7 个非生物特效/下落方块/核弹实体被误设为 MobCategory.MONSTER 导致末地怪物刷怪配额被占满的问题。
 * 注意：register 在目标类静态初始化阶段被调用，禁止在此 Mixin 中依赖末尾才初始化的静态集合字段。
 */
@Mixin(value = EnlightenedEndModEntities.class, remap = false)
public abstract class EnlightenedEndModEntitiesMixin {
    @Inject(method = "register", at = @At("HEAD"))
    private static <T extends Entity> void enlightendbugfix$fixNonMobCategory(
            String registryname,
            EntityType.Builder<T> entityTypeBuilder,
            CallbackInfoReturnable<RegistryObject<EntityType<T>>> cir
    ) {
        if (enlightendbugfix$isNonMobMiscEntity(registryname)) {
            EntityTypeBuilderAccessor accessor = (EntityTypeBuilderAccessor) entityTypeBuilder;
            accessor.enlightendbugfix$setCategory(MobCategory.MISC);
            accessor.enlightendbugfix$setCanSpawnFarFromPlayer(true);
        }
    }

    @Unique
    private static boolean enlightendbugfix$isNonMobMiscEntity(String registryname) {
        if (registryname == null) {
            return false;
        }
        return switch (registryname) {
            case "floating_elevibloom",
                 "activated_nuclear_bomb",
                 "falling_bounceling_nut",
                 "disturbed_helium_gas",
                 "disturbed_xenon_gas",
                 "falling_heliosand",
                 "ooze_bubble" -> true;
            default -> false;
        };
    }
}
