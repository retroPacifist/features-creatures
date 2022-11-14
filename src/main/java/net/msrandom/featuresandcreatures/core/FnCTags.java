package net.msrandom.featuresandcreatures.core;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

public class FnCTags
{
    public static class Blocks
    {
        public static final TagKey<Block> SHULKREN_YOUNGLING_SAPLINGS = create("shulkren_youngling_saplings");

        private static TagKey<Block> create(String id)
        {
            return TagKey.create(Registry.BLOCK_REGISTRY, FeaturesAndCreatures.createResourceLocation(id));
        }

        public static Optional<Block> getRandomElement(TagKey<Block> tag, Random random)
        {
            return FnCTags.getRandomElement(ForgeRegistries.BLOCKS, tag, random);
        }
    }

    private static <T extends IForgeRegistryEntry<T>> Optional<T> getRandomElement(IForgeRegistry<T> registry, TagKey<T> tag, Random random)
    {
        return Objects.requireNonNull(registry.tags()).getTag(tag).getRandomElement(random);
    }

}
