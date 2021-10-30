package net.msrandom.featuresandcreatures;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.BitArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.msrandom.featuresandcreatures.client.renderer.entity.JockeyRenderer;
import net.msrandom.featuresandcreatures.entity.FnAEntities;
import net.msrandom.featuresandcreatures.entity.JockeyEntity;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

@Mod(FeaturesAndCreatures.MOD_ID)
public class FeaturesAndCreatures {
    private static final long JOCKEY_SPAWN_TIME = 216000L;
    public static final String MOD_ID = "featuresandcreatures";

    public FeaturesAndCreatures() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::registerAttributes);
        FnAEntities.REGISTRAR.register(bus);
        MinecraftForge.EVENT_BUS.addGenericListener(World.class, FeaturesAndCreatures::attachCapabilities);
        MinecraftForge.EVENT_BUS.addListener(FeaturesAndCreatures::tickWorld);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(WorldJockeyCapability.class, new WorldJockeyCapability.Storage(), WorldJockeyCapability::new);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(FnAEntities.JOCKEY.get(), JockeyRenderer::new);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FnAEntities.JOCKEY.get(), JockeyEntity.createJockeyAttributes().build());
    }

    private static void attachCapabilities(AttachCapabilitiesEvent<World> event) {
        event.addCapability(WorldJockeyCapability.ID, new WorldJockeyCapability());
    }

    private static void tickWorld(TickEvent.WorldTickEvent event) {
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
                                HorseEntity horse = EntityType.HORSE.create(event.world);
                                if (horse != null) {
                                    JockeyEntity jockey = FnAEntities.JOCKEY.get().create(event.world);
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
