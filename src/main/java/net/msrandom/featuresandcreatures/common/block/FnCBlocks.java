package net.msrandom.featuresandcreatures.common.block;

import lombok.experimental.UtilityClass;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

@UtilityClass
public class FnCBlocks {
    public final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, FeaturesAndCreatures.MOD_ID);

    public final RegistryObject<Block> DAWN_ORE = REGISTER.register("dawn_ore", () -> createOre(FeaturesCreaturesOre.Duration.DAWN));
    public final RegistryObject<Block> SUNSET_ORE = REGISTER.register("sunset_ore", () -> createOre(FeaturesCreaturesOre.Duration.SUNSET));
    public final RegistryObject<Block> MIDNIGHT_ORE = REGISTER.register("midnight_ore", () -> createOre(FeaturesCreaturesOre.Duration.MIDNIGHT));

    public final RegistryObject<Block> DEEPSLATE_DAWN_ORE = REGISTER.register("deepslate_dawn_ore", () -> createOre(FeaturesCreaturesOre.Duration.DAWN));
    public final RegistryObject<Block> DEEPSLATE_SUNSET_ORE = REGISTER.register("deepslate_sunset_ore", () -> createOre(FeaturesCreaturesOre.Duration.SUNSET));
    public final RegistryObject<Block> DEEPSLATE_MIDNIGHT_ORE = REGISTER.register("deepslate_midnight_ore", () -> createOre(FeaturesCreaturesOre.Duration.MIDNIGHT));

    public final RegistryObject<Block> DAWN_BLOCK = REGISTER.register("dawn_block", FnCBlocks::createBlock);
    public final RegistryObject<Block> SUNSET_BLOCK = REGISTER.register("sunset_block", FnCBlocks::createBlock);
    public final RegistryObject<Block> MIDNIGHT_BLOCK = REGISTER.register("midnight_block", FnCBlocks::createBlock);

    private Block createOre(FeaturesCreaturesOre.Duration duration) {
        return new FeaturesCreaturesOre(duration, Properties.copy(Blocks.DIAMOND_ORE));
    }

    private Block createBlock() {
        return new Block(Properties.copy(Blocks.DIAMOND_BLOCK));
    }
}
