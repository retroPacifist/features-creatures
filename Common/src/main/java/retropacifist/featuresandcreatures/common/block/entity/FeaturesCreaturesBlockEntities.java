package retropacifist.featuresandcreatures.common.block.entity;

import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.block.FeaturesCreaturesBlocks;
import retropacifist.reg.RegistrationProvider;
import retropacifist.reg.RegistryObject;

@SuppressWarnings("ConstantConditions")
@UtilityClass
public class FeaturesCreaturesBlockEntities {
    public static final RegistrationProvider<BlockEntityType<?>> PROVIDER = RegistrationProvider.get(Registry.BLOCK_ENTITY_TYPE, FeaturesAndCreatures.MOD_ID);

    public final RegistryObject<BlockEntityType<FeaturesCreaturesOreBlockEntity>> ORE = PROVIDER.register("ore", () -> BlockEntityType.Builder.of(FeaturesCreaturesOreBlockEntity::new,
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

    public static void bootStrap() {
    }
}
