package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.block.entity.AncientStalk1BlockEntity;
import net.mcreator.enlightened_end.block.entity.AncientStalk2BlockEntity;
import net.mcreator.enlightened_end.block.entity.AncientStalk3BlockEntity;
import net.mcreator.enlightened_end.block.entity.CeruleanStalk0BlockEntity;
import net.mcreator.enlightened_end.block.entity.CeruleanVineBlockEntity;
import net.mcreator.enlightened_end.block.entity.CeruleanVineTipBlockEntity;
import net.mcreator.enlightened_end.block.entity.ClosedMalachiteCapsuleBlockEntity;
import net.mcreator.enlightened_end.block.entity.EnnegelBlockBlockEntity;
import net.mcreator.enlightened_end.block.entity.LaserBlockEntity;
import net.mcreator.enlightened_end.block.entity.LaserEmissionBlockEntity;
import net.mcreator.enlightened_end.block.entity.LaserOnBlockEntity;
import net.mcreator.enlightened_end.block.entity.MalachiteCapsuleBlockEntity;
import net.mcreator.enlightened_end.block.entity.NuclearFurnaceBlockEntity;
import net.mcreator.enlightened_end.block.entity.SacrificalStatueBlockEntity;
import net.mcreator.enlightened_end.block.entity.SpringLeafDeactivatedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 将终末之光全部 15 个方块实体写死英文的 getDefaultName / getDisplayName 改为复用对应方块的原生翻译键名称。
 */
@Mixin({
        AncientStalk1BlockEntity.class,
        AncientStalk2BlockEntity.class,
        AncientStalk3BlockEntity.class,
        CeruleanStalk0BlockEntity.class,
        CeruleanVineBlockEntity.class,
        CeruleanVineTipBlockEntity.class,
        ClosedMalachiteCapsuleBlockEntity.class,
        EnnegelBlockBlockEntity.class,
        LaserBlockEntity.class,
        LaserEmissionBlockEntity.class,
        LaserOnBlockEntity.class,
        MalachiteCapsuleBlockEntity.class,
        NuclearFurnaceBlockEntity.class,
        SacrificalStatueBlockEntity.class,
        SpringLeafDeactivatedBlockEntity.class
})
public abstract class BlockEntityDefaultNameMixin extends RandomizableContainerBlockEntity {
    protected BlockEntityDefaultNameMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getDefaultName", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$localizedDefaultName(CallbackInfoReturnable<Component> cir) {
        cir.setReturnValue(this.getBlockState().getBlock().getName());
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void enlightendbugfix$localizedDisplayName(CallbackInfoReturnable<Component> cir) {
        cir.setReturnValue(this.hasCustomName() ? this.getCustomName() : this.getBlockState().getBlock().getName());
    }
}
