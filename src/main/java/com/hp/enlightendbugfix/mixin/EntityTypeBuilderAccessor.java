package com.hp.enlightendbugfix.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 访问并修正 EntityType.Builder 内部的分类字段。
 */
@Mixin(EntityType.Builder.class)
public interface EntityTypeBuilderAccessor {
    @Mutable
    @Accessor("category")
    void enlightendbugfix$setCategory(MobCategory category);

    @Mutable
    @Accessor("canSpawnFarFromPlayer")
    void enlightendbugfix$setCanSpawnFarFromPlayer(boolean canSpawnFarFromPlayer);
}
