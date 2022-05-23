package net.msrandom.featuresandcreatures.common.block.entity;

import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.block.FeaturesCreaturesBlocks;
import retropacifist.featuresandcreatures.reg.RegistrationProvider;
import retropacifist.featuresandcreatures.reg.RegistryObject;

@SuppressWarnings("ConstantConditions")
@UtilityClass
public class FeaturesCreaturesBlockEntities {
    public static final RegistrationProvider<BlockEntityType<?>> REGISTER = RegistrationProvider.get(Registry.BLOCK_ENTITY_TYPE, FeaturesAndCreatures.MOD_ID);

    public final RegistryObject<BlockEntityType<FeaturesCreaturesOreBlockEntity>> ORE = REGISTER.register("ore", () -> BlockEntityType.Builder.of(FeaturesCreaturesOreBlockEntity::new,
            FeaturesCreaturesBlocks.STONE_DAWN_ORE.get(),
            FeaturesCreaturesBlocks.STONE_SUNSET_ORE.get(),
            FeaturesCreaturesBlocks.STONE_MIDNIGHT_ORE.get(),
            FeaturesCreaturesBlocks.DAWN_ORE.get(),
            FeaturesCreaturesBlocks.SUNSET_ORE.get(),
            FeaturesCreaturesBlocks.MIDNIGHT_ORE.get(),
            FeaturesCreaturesBlocks.DEEPSLATE_DAWN_ORE.get(),
            FeaturesCreaturesBlocks.DEEPSLATE_MIDNIGHT_ORE.get(),
            FeaturesCreaturesBlocks.DEEPSLATE_SUNSET_ORE.get()
    ).build(null));
}
