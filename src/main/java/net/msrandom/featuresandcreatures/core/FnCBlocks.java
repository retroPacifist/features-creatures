package net.msrandom.featuresandcreatures.core;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

public class FnCBlocks {
    public static final DeferredRegister<Block> REGISTRAR = DeferredRegister.create(ForgeRegistries.BLOCKS, FeaturesAndCreatures.MOD_ID);

    public static final RegistryObject<Block> SUNSET_ORE = createOre("sunset_ore");
    public static final RegistryObject<Block> DAWN_ORE = createOre("dawn_ore");
    public static final RegistryObject<Block> MIDNIGHT_ORE = createOre("midnight_ore");
    public static final RegistryObject<Block> STONE_SUNSET_ORE = createOre("stone_sunset_ore");
    public static final RegistryObject<Block> STONE_DAWN_ORE = createOre("stone_dawn_ore");
    public static final RegistryObject<Block> STONE_MIDNIGHT_ORE = createOre("stone_midnight_ore");
    public static final RegistryObject<Block> DEEPSLATE_SUNSET_ORE = createOre("deepslate_sunset_ore");
    public static final RegistryObject<Block> DEEPSLATE_DAWN_ORE = createOre("deepslate_dawn_ore");
    public static final RegistryObject<Block> DEEPSLATE_MIDNIGHT_ORE = createOre("deepslate_midnight_ore");
    public static final RegistryObject<Block> SUNSET_CRYSTAL_BLOCK = createOreBlock("sunset_crystal_block");
    public static final RegistryObject<Block> DAWN_CRYSTAL_BLOCK = createOreBlock("dawn_crystal_block");
    public static final RegistryObject<Block> MIDNIGHT_CRYSTAL_BLOCK = createOreBlock("midnight_crystal_block");

    public static RegistryObject<Block> createOre(String id){
        return REGISTRAR.register(id, () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_ORE)));
    }
    public static RegistryObject<Block> createOreBlock(String id){
        return REGISTRAR.register(id, () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
    }
}
