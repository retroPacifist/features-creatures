package retropacifist.featuresandcreatures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import retropacifist.featuresandcreatures.common.entity.BlackForestSpirit;
import retropacifist.featuresandcreatures.common.entity.Jockey;
import retropacifist.featuresandcreatures.core.FnCEntities;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = FeaturesAndCreatures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FnCForgeEventsHandler {

    //TODO: use tags
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void spawnMobs(BiomeLoadingEvent event) {
        Biome.BiomeCategory category = event.getCategory();
        switch (category) {
            case THEEND:
                event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.SHULKREN_YOUNGLING.get(), 100, 1, 1));
            case FOREST:
                event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(FnCEntities.BLACK_FOREST_SPIRIT.get(), 100, 1, 1));
                event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.TBH.get(), 10, 3, 6));
            case NETHER:
                event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(FnCEntities.BRIMSTONE_GOLEM.get(), 1000, 1, 1));
            case SWAMP:
                event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.GUP.get(), 100, 1, 4));
            case PLAINS:
                event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.TBH.get(), 10, 3, 6));
        }
        if (Objects.equals(event.getName(), new ResourceLocation("byg", "shulkren_forest"))) {
            event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.SHULKREN_YOUNGLING.get(), 100, 4, 9));
        }
        if (Objects.equals(event.getName(), new ResourceLocation("byg", "fir_forest"))) {
            event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(FnCEntities.BLACK_FOREST_SPIRIT.get(), 100, 4, 9));
        }
        if (category != Biome.BiomeCategory.THEEND && category != Biome.BiomeCategory.NETHER) {
            event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(FnCEntities.GUP.get(), 11, 1, 4));
        }
    }

    @SubscribeEvent
    public static void addTargetOnBlockBreak(BlockEvent.BreakEvent event){
        Player player = event.getPlayer();
        if (event.getState().getBlock().getRegistryName().toString().contains("log")){
            List<BlackForestSpirit> list = player.level.getEntitiesOfClass(BlackForestSpirit.class, new AABB(player.blockPosition()).inflate(30));
            if (list.isEmpty()) return;
            for (BlackForestSpirit spirit : list){
                spirit.setPersistentAngerTarget(player.getUUID());
                spirit.setRemainingPersistentAngerTime(500);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttacked(LivingAttackEvent event) {
        LivingEntity target = event.getEntityLiving();

        if (target instanceof Jockey && target.equals(event.getSource().getEntity())) {
            event.setCanceled(true);
            return;
        }

        if (target.hasPassenger((passenger) -> passenger instanceof Jockey))
        {

            target.getPassengers().stream().filter(entity -> entity instanceof Jockey).forEach(jockey ->
            {
                if (jockey.equals(event.getSource().getEntity())) {
                    event.setCanceled(true);
                }
            });
        }
    }
}