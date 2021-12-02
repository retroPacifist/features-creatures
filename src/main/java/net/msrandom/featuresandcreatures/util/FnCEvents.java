package net.msrandom.featuresandcreatures.util;

import net.minecraft.command.Commands;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.entities.jackalope.Jackalope;
import net.msrandom.featuresandcreatures.common.entities.jockey.Jockey;
import net.msrandom.featuresandcreatures.common.entities.sabertooth.Sabertooth;
import net.msrandom.featuresandcreatures.core.FnCEntities;

@Mod.EventBusSubscriber(modid = FeaturesAndCreatures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FnCEvents {
    private static final long JOCKEY_SPAWN_TIME = 216000L;

    @SubscribeEvent
    public static void tickWorld(TickEvent.WorldTickEvent event) {
        if (!event.world.isClientSide) {
            event.world.getCapability(WorldJockeyCapability.capability).ifPresent(capability -> {
                if (event.world.getGameTime() - capability.getSpawnAttemptTime() >= JOCKEY_SPAWN_TIME) {
                    spawnJockey(event.world, capability);
                }
            });
        }
    }

    public static void spawnJockey(World world, WorldJockeyCapability capability) {
        // TODO don't check dimension like this, I hate everything about this.
        if (world.dimension().location().getPath().equals("overworld")) {
            if (!capability.isSpawned()) {
                capability.markSpawnAttempt();
                ServerPlayerEntity player = ((ServerWorld) world).getRandomPlayer();
                if (player != null) {
                    BlockPos position = player.blockPosition();
                    int availableBlocks = 0;
                    for (BlockPos blockPos : BlockPos.betweenClosed(position.offset(-2, -1, -2), position.offset(2, 3, 2))) {
                        if (world.isEmptyBlock(blockPos)) {
                            if (++availableBlocks == 27) break;
                        }
                    }
                    if (availableBlocks == 27) {
                        // FIXME use appropriate mount based on biome
                        Jackalope jackalope = FnCEntities.JACKALOPE.get().create(world);
                        if (jackalope != null) {
                            Jockey jockey = FnCEntities.JOCKEY.get().create(world);
                            if (jockey != null) {
                                jackalope.setSaddled(true);
                                Vector3d lookAngle = player.getLookAngle();
                                Vector3d offset = new Vector3d(lookAngle.x(), 0, lookAngle.z()).normalize().scale(-1.5);
                                jackalope.moveTo(position.getX() + 0.5 + offset.x(), position.getY() + 1, position.getZ() + 0.5 + offset.z());
                                jackalope.finalizeSpawn((IServerWorld) world, world.getCurrentDifficultyAt(position), SpawnReason.NATURAL, null, null);
                                jockey.moveTo(jackalope.getX(), jackalope.getY(), jackalope.getZ());
                                jockey.finalizeSpawn((IServerWorld) world, world.getCurrentDifficultyAt(position), SpawnReason.NATURAL, null, null);
                                jockey.startRiding(jackalope);
                                world.addFreshEntity(jackalope);
                                world.addFreshEntity(jockey);
                                capability.setSpawned(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("simulatejockeyspawn")
                        .requires(source -> source.hasPermission(2))
                        .executes(source -> {
                            source.getSource().getLevel()
                                    .getCapability(WorldJockeyCapability.capability)
                                    .ifPresent(capability -> spawnJockey(source.getSource().getLevel(), capability));
                            return 0;
                        })
        );
    }

    @SubscribeEvent
    public static void joinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof WolfEntity || entity instanceof CreeperEntity) {
            ((CreatureEntity) entity).goalSelector.addGoal(3, new AvoidEntityGoal<>((CreatureEntity) entity, Sabertooth.class, 6.0F, 1.0D, 1.2D));
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<World> event) {
        event.addCapability(WorldJockeyCapability.ID, new WorldJockeyCapability(event.getObject()));
    }
}
