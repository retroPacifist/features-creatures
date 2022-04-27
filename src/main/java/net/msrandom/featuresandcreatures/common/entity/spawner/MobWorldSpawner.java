package net.msrandom.featuresandcreatures.common.entity.spawner;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.core.FnCEntities;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = FeaturesAndCreatures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobWorldSpawner {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void spawnMobs(BiomeLoadingEvent event) {
        if (event.getCategory() == Biome.BiomeCategory.THEEND && !Objects.equals(event.getName(), new ResourceLocation("the_end"))) {
            event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.SHULKREN_YOUNGLING.get(), 100, 1, 1));
        }
        if (Objects.equals(event.getName(), new ResourceLocation("byg", "shulkren_forest"))){
            event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.SHULKREN_YOUNGLING.get(), 100, 4, 9));
        }
        if (event.getCategory() == Biome.BiomeCategory.FOREST) {
            event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(FnCEntities.BLACK_FOREST_SPIRIT.get(), 100, 1, 1));
        }
        if (event.getCategory() == Biome.BiomeCategory.NETHER) {
            event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(FnCEntities.BRIMSTONE_GOLEM.get(), 1000, 1, 1));
        }
        if (event.getCategory() == Biome.BiomeCategory.SWAMP) {
            event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.GUP.get(), 100, 1, 4));
        }
        if (event.getCategory() != Biome.BiomeCategory.THEEND && event.getCategory() != Biome.BiomeCategory.NETHER) {
            event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(FnCEntities.GUP.get(), 11, 1, 4));
        }
        if (event.getCategory() == Biome.BiomeCategory.PLAINS) {
            event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.TBH.get(), 10, 3, 6));
        }
        if (event.getCategory() == Biome.BiomeCategory.FOREST) {
            event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.TBH.get(), 10, 3, 6));
        }
    }
}
