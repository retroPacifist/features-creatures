package net.msrandom.featuresandcreatures;

import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.msrandom.featuresandcreatures.common.entities.boar.Boar;
import net.msrandom.featuresandcreatures.common.entities.boar.BoarRenderer;
import net.msrandom.featuresandcreatures.common.entities.jackalope.Jackalope;
import net.msrandom.featuresandcreatures.common.entities.jackalope.JackalopeRenderer;
import net.msrandom.featuresandcreatures.common.entities.jockey.JockeyRenderer;
import net.msrandom.featuresandcreatures.common.entities.sabertooth.Sabertooth;
import net.msrandom.featuresandcreatures.common.entities.sabertooth.SabertoothRenderer;
import net.msrandom.featuresandcreatures.core.FnAEntities;
import net.msrandom.featuresandcreatures.common.entities.jockey.Jockey;
import net.msrandom.featuresandcreatures.util.WorldJockeyCapability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

@Mod(FeaturesAndCreatures.MOD_ID)
public class FeaturesAndCreatures {
    public static final String MOD_ID = "featuresandcreatures";
    public static final Logger LOGGER = LogManager.getLogger();

    public FeaturesAndCreatures() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::registerAttributes);
        MinecraftForge.EVENT_BUS.addGenericListener(World.class, FeaturesAndCreatures::attachCapabilities);
        GeckoLib.initialize();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(WorldJockeyCapability.class, new WorldJockeyCapability.Storage(), WorldJockeyCapability::new);
    }

    private void clientSetup(FMLClientSetupEvent event) {
       registerRenderers();
    }

    public void registerRenderers(){
        RenderingRegistry.registerEntityRenderingHandler(FnAEntities.JOCKEY, JockeyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnAEntities.BOAR, BoarRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnAEntities.JACKALOPE, JackalopeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnAEntities.SABERTOOTH, SabertoothRenderer::new);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FnAEntities.JOCKEY, Jockey.createJockeyAttributes().build());
        event.put(FnAEntities.BOAR, Boar.createAttributes().build());
        event.put(FnAEntities.JACKALOPE, Jackalope.createAttributes().build());
        event.put(FnAEntities.SABERTOOTH, Sabertooth.createAttributes().build());
    }

    private static void attachCapabilities(AttachCapabilitiesEvent<World> event) {
        event.addCapability(WorldJockeyCapability.ID, new WorldJockeyCapability());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
            LOGGER.info("HELLO from Register Entities");
            FnAEntities.init();
            FnAEntities.entities.forEach(entity -> event.getRegistry().register(entity));
            FnAEntities.entities.clear();
            FnAEntities.entities = null;
            LOGGER.info("GOODBYE from Register Entities");
        }
    }
}
