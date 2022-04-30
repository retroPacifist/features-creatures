package net.msrandom.featuresandcreatures.common.block.entity;

import lombok.experimental.UtilityClass;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.block.FeaturesCreaturesBlocks;

@SuppressWarnings("ConstantConditions")
@UtilityClass
public class FeaturesCreaturesBlockEntities {
    public final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, FeaturesAndCreatures.MOD_ID);

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
