package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.init.EnlightenedEndModBlocks;
import net.mcreator.enlightened_end.init.EnlightenedEndModCompostableItems;
import net.mcreator.enlightened_end.init.EnlightenedEndModItems;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 FMLCommonSetupEvent 并行阶段直接裸写 ComposterBlock.COMPOSTABLES 引发的多线程并发写坏风险。
 */
@Mixin(value = EnlightenedEndModCompostableItems.class, remap = false)
public abstract class CompostableItemsMixin {
    @Inject(method = "addComposterItems", at = @At("HEAD"), cancellable = true)
    private static void enlightendbugfix$enqueueComposterRegistration(FMLCommonSetupEvent event, CallbackInfo ci) {
        event.enqueueWork(() -> {
            synchronized (ComposterBlock.COMPOSTABLES) {
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.SPRINGLEAF.get().asItem(), 0.65F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModItems.FROST_FRUIT.get(), 0.45F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModItems.ANCIENT_ROOT.get(), 0.65F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModItems.ELEVIBLOOM.get(), 0.1F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.GLOWSEEP.get().asItem(), 0.65F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModItems.ELEVIBLOOM_SEEDS.get(), 0.3F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.INDIGO_ROOTS.get().asItem(), 0.65F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.INDIGO_SPROUTS.get().asItem(), 0.5F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.CERULEAN_VINE.get().asItem(), 0.5F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.CERULEAN_GRASS.get().asItem(), 0.3F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModItems.SQUISHED_BOUNCER.get(), 0.2F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.ENNEGEL_FERN.get().asItem(), 0.3F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.CERULEAN_FROND.get().asItem(), 0.3F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModItems.DOUBLE_CERULEAN_GRASS.get(), 0.5F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.GLOW_GOURD.get().asItem(), 0.65F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.CLOSED_GLOW_GOURD.get().asItem(), 0.65F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModItems.GLOW_GOURD_SLICE.get(), 0.5F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.CRADLING_FLOWER.get().asItem(), 0.65F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModItems.CERULEAN_STALK.get(), 0.4F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.CLOSED_ODD_ROOTS.get().asItem(), 0.65F);
                ComposterBlock.COMPOSTABLES.put(EnlightenedEndModBlocks.OPEN_ODD_ROOTS.get().asItem(), 0.65F);
            }
        });
        ci.cancel();
    }
}
