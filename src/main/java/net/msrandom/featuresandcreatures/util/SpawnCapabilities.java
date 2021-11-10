package net.msrandom.featuresandcreatures.util;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.entities.jackalope.Jackalope;
import net.msrandom.featuresandcreatures.common.entities.jockey.Jockey;
import net.msrandom.featuresandcreatures.core.FnCEntities;

@Mod.EventBusSubscriber(modid = FeaturesAndCreatures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpawnCapabilities {
    private static final long JOCKEY_SPAWN_TIME = 216000L;

    @SubscribeEvent
    public static void tickWorld(TickEvent.WorldTickEvent event) {
        if (!event.world.isClientSide && event.world.getGameTime() % JOCKEY_SPAWN_TIME == 0) {
            // TODO don't check dimension like this, I hate everything about this.
            if (event.world.dimension().location().getPath().equals("overworld")) {
                event.world.getCapability(WorldJockeyCapability.capability).ifPresent(capability -> {
                    if (!capability.isSpawned()) {
                        ServerPlayerEntity player = ((ServerWorld) event.world).getRandomPlayer();
                        if (player != null) {
                            BlockPos position = player.blockPosition();
                            int availableBlocks = 0;
                            for (BlockPos blockPos : BlockPos.betweenClosed(position.offset(-2, -1, -2), position.offset(2, 3, 2))) {
                                if (event.world.isEmptyBlock(blockPos)) {
                                    if (++availableBlocks == 27) break;
                                }
                            }
                            if (availableBlocks == 27) {
                                // FIXME use appropriate mount based on biome
                                Jackalope horse = FnCEntities.JACKALOPE.get().create(event.world);
                                if (horse != null) {
                                    Jockey jockey = FnCEntities.JOCKEY.get().create(event.world);
                                    if (jockey != null) {
                                        horse.setBaby(true);
                                        Vector3d lookAngle = player.getLookAngle();
                                        Vector3d offset = new Vector3d(lookAngle.x(), 0, lookAngle.z()).normalize().scale(-1.5);
                                        horse.moveTo(position.getX() + 0.5 + offset.x(), position.getY() + 1, position.getZ() + 0.5 + offset.z());
                                        horse.finalizeSpawn((IServerWorld) event.world, event.world.getCurrentDifficultyAt(position), SpawnReason.NATURAL, null, null);
                                        jockey.moveTo(horse.getX(), horse.getY(), horse.getZ());
                                        jockey.finalizeSpawn((IServerWorld) event.world, event.world.getCurrentDifficultyAt(position), SpawnReason.NATURAL, null, null);
                                        jockey.startRiding(horse);
                                        event.world.addFreshEntity(horse);
                                        event.world.addFreshEntity(jockey);
                                        capability.setSpawned(true);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}
