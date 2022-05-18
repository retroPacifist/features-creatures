package retropacifist.featuresandcreatures.common.block;

import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.reg.BlockRegistryObject;
import retropacifist.reg.RegistrationProvider;

import java.util.function.Supplier;

// Please do not rename to "FnCBlocks"
@UtilityClass
public class FeaturesCreaturesBlocks {
    public static final RegistrationProvider<Block> PROVIDER = RegistrationProvider.get(Registry.BLOCK, FeaturesAndCreatures.MOD_ID);

    public final BlockRegistryObject<Block> STONE_DAWN_ORE = createBlock("stone_dawn_ore", createOre(FeaturesCreaturesOre.Duration.DAWN));
    public final BlockRegistryObject<Block> STONE_SUNSET_ORE = createBlock("stone_sunset_ore", createOre(FeaturesCreaturesOre.Duration.SUNSET));
    public final BlockRegistryObject<Block> STONE_MIDNIGHT_ORE = createBlock("stone_midnight_ore", createOre(FeaturesCreaturesOre.Duration.MIDNIGHT));

    public final BlockRegistryObject<Block> DAWN_ORE = createBlock("dawn_ore", createOre(FeaturesCreaturesOre.Duration.DAWN));
    public final BlockRegistryObject<Block> SUNSET_ORE = createBlock("sunset_ore", createOre(FeaturesCreaturesOre.Duration.SUNSET));
    public final BlockRegistryObject<Block> MIDNIGHT_ORE = createBlock("midnight_ore", createOre(FeaturesCreaturesOre.Duration.MIDNIGHT));

    public final BlockRegistryObject<Block> DEEPSLATE_DAWN_ORE = createBlock("deepslate_dawn_ore", createOre(FeaturesCreaturesOre.Duration.DAWN));
    public final BlockRegistryObject<Block> DEEPSLATE_SUNSET_ORE = createBlock("deepslate_sunset_ore", createOre(FeaturesCreaturesOre.Duration.SUNSET));
    public final BlockRegistryObject<Block> DEEPSLATE_MIDNIGHT_ORE = createBlock("deepslate_midnight_ore", createOre(FeaturesCreaturesOre.Duration.MIDNIGHT));

    public final BlockRegistryObject<Block> DAWN_BLOCK = createBlock("dawn_block", FeaturesCreaturesBlocks::createBlock);
    public final BlockRegistryObject<Block> SUNSET_BLOCK = createBlock("sunset_block", FeaturesCreaturesBlocks::createBlock);
    public final BlockRegistryObject<Block> MIDNIGHT_BLOCK = createBlock("midnight_block", FeaturesCreaturesBlocks::createBlock);

    private Supplier<Block> createOre(FeaturesCreaturesOre.Duration duration) {
        return () -> new FeaturesCreaturesOre(duration, Properties.copy(Blocks.DIAMOND_ORE));
    }

    private Block createBlock() {
        return new Block(Properties.copy(Blocks.DIAMOND_BLOCK));
    }

    public static <B extends Block> BlockRegistryObject<B> createBlock(String id, Supplier<? extends B> block) {
        final var ro = PROVIDER.<B>register(id, block);
        return BlockRegistryObject.wrap(ro);
    }

    public static void bootStrap() {
    }
}
