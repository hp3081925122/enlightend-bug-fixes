package com.hp.enlightendbugfix.mixin;

import net.mcreator.enlightened_end.procedures.PlanetaryAirBlockAddedProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 MCR-54：PlanetaryAirBlockAddedProcedure 在 for 循环条件中每轮重算 Mth.nextInt(RandomSource.create(), 8, 14)，
 * 同时在数十个分支中重复调用 getOrCreate 放置巨行星与小行星模板。
 * 改为循环前置求值、复用服务端随机源，并使用 StructureTemplateManager#get 安全加载模板。
 */
@Mixin(value = PlanetaryAirBlockAddedProcedure.class, remap = false)
public abstract class PlanetaryAirLoopFixMixin {

    @Unique
    private static final ResourceLocation SMALL_END_ISLANDS = new ResourceLocation("small_end_islands");
    @Unique
    private static final ResourceLocation XENON_GIANT_1 = new ResourceLocation("enlightened_end", "xenon_giant_1");
    @Unique
    private static final ResourceLocation XENON_GIANT_2 = new ResourceLocation("enlightened_end", "xenon_giant_2");
    @Unique
    private static final ResourceLocation XENON_GIANT_3 = new ResourceLocation("enlightened_end", "xenon_giant_3");
    @Unique
    private static final ResourceLocation XENON_GIANT_4 = new ResourceLocation("enlightened_end", "xenon_giant_4");
    @Unique
    private static final ResourceLocation HELIUM_GIANT_1 = new ResourceLocation("enlightened_end", "helium_giant_1");
    @Unique
    private static final ResourceLocation HELIUM_GIANT_2 = new ResourceLocation("enlightened_end", "helium_giant_2");
    @Unique
    private static final ResourceLocation HELIUM_GIANT_3 = new ResourceLocation("enlightened_end", "helium_giant_3");
    @Unique
    private static final ResourceLocation HELIUM_GIANT_4 = new ResourceLocation("enlightened_end", "helium_giant_4");
    @Unique
    private static final ResourceLocation ASTEROID_1 = new ResourceLocation("enlightened_end", "nullstone_asteroid_1");
    @Unique
    private static final ResourceLocation ASTEROID_2 = new ResourceLocation("enlightened_end", "nullstone_asteroid_2");
    @Unique
    private static final ResourceLocation ASTEROID_3 = new ResourceLocation("enlightened_end", "nullstone_asteroid_3");
    @Unique
    private static final ResourceLocation ASTEROID_4 = new ResourceLocation("enlightened_end", "nullstone_asteroid_4");

    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDD)V", at = @At("HEAD"), cancellable = true)
    private static void efFix$fixPlanetaryAirLoop(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        ci.cancel();
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        RandomSource random = serverLevel.getRandom();
        BlockPos origin = BlockPos.containing(x, y, z);
        boolean isXenon = Mth.nextInt(random, 1, 2) == 1;

        int giantVariant = Mth.nextInt(random, 1, 3);
        if (giantVariant == 1) {
            efFix$placeTemplate(serverLevel, isXenon ? XENON_GIANT_1 : HELIUM_GIANT_1, origin, Rotation.getRandom(random));
        } else if (giantVariant == 2) {
            efFix$placeTemplate(serverLevel, isXenon ? XENON_GIANT_2 : HELIUM_GIANT_2, origin, Rotation.NONE);
        } else {
            efFix$placeTemplate(serverLevel, isXenon ? XENON_GIANT_3 : HELIUM_GIANT_3, origin, Rotation.NONE);
        }

        int asteroidCount = Mth.nextInt(random, 8, 14);
        for (int i = 0; i < asteroidCount; i++) {
            int pick = Mth.nextInt(random, 1, 4);
            double movex = random.nextBoolean() ? x + Mth.nextInt(random, 8, 24) : x - Mth.nextInt(random, 8, 24);
            double movey = random.nextBoolean() ? y + Mth.nextInt(random, 8, 12) : y - Mth.nextInt(random, 8, 12);
            double movez = random.nextBoolean() ? z + Mth.nextInt(random, 8, 24) : z - Mth.nextInt(random, 8, 24);
            BlockPos target = BlockPos.containing(movex, movey, movez);
            if (!serverLevel.getBiome(target).is(SMALL_END_ISLANDS)) {
                continue;
            }
            ResourceLocation templateId;
            if (pick == 1) {
                if (random.nextBoolean()) {
                    templateId = ASTEROID_1;
                } else {
                    templateId = isXenon ? XENON_GIANT_4 : HELIUM_GIANT_4;
                }
            } else if (pick == 2) {
                templateId = ASTEROID_2;
            } else if (pick == 3) {
                templateId = ASTEROID_3;
            } else {
                templateId = ASTEROID_4;
            }
            efFix$placeTemplate(serverLevel, templateId, target, Rotation.getRandom(random));
        }

        serverLevel.setBlock(origin, Blocks.AIR.defaultBlockState(), 3);
    }

    @Unique
    private static void efFix$placeTemplate(ServerLevel serverLevel, ResourceLocation templateId, BlockPos pos, Rotation rotation) {
        serverLevel.getStructureManager().get(templateId).ifPresent(template ->
                template.placeInWorld(
                        serverLevel,
                        pos,
                        pos,
                        new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE).setIgnoreEntities(false),
                        serverLevel.random,
                        3
                )
        );
    }
}
