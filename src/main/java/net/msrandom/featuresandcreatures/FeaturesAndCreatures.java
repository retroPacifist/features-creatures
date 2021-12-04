package net.msrandom.featuresandcreatures;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.msrandom.featuresandcreatures.common.entities.boar.Boar;
import net.msrandom.featuresandcreatures.common.entities.boar.BoarRenderer;
import net.msrandom.featuresandcreatures.common.entities.jackalope.Jackalope;
import net.msrandom.featuresandcreatures.common.entities.jackalope.JackalopeRenderer;
import net.msrandom.featuresandcreatures.common.entities.jockey.Jockey;
import net.msrandom.featuresandcreatures.common.entities.jockey.JockeyRenderer;
import net.msrandom.featuresandcreatures.common.entities.sabertooth.Sabertooth;
import net.msrandom.featuresandcreatures.common.entities.sabertooth.SabertoothRenderer;
import net.msrandom.featuresandcreatures.common.entities.spear.SpearRenderer;
import net.msrandom.featuresandcreatures.common.items.antler_headdress.AntlerHeadDressItem;
import net.msrandom.featuresandcreatures.common.items.antler_headdress.AntlerHeadDressRenderer;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCItems;
import net.msrandom.featuresandcreatures.core.FnCKeybinds;
import net.msrandom.featuresandcreatures.core.FnCTriggers;
import net.msrandom.featuresandcreatures.util.WorldJockeyCapability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

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
        FnCEntities.REGISTRAR.register(bus);
        FnCItems.REGISTRAR.register(bus);
        FnCTriggers.register();

        //GeckoLib
        GeckoLib.initialize();
    }

    private static void attachCapabilities(AttachCapabilitiesEvent<World> event) {
        event.addCapability(WorldJockeyCapability.ID, new WorldJockeyCapability());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(WorldJockeyCapability.class, new WorldJockeyCapability.Storage(), WorldJockeyCapability::new);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        registerRenderers();
        FnCKeybinds.register();
    }

    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(FnCEntities.JOCKEY.get(), JockeyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnCEntities.BOAR.get(), BoarRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnCEntities.JACKALOPE.get(), JackalopeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnCEntities.SABERTOOTH.get(), SabertoothRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnCEntities.SPEAR.get(), SpearRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(AntlerHeadDressItem.class, new AntlerHeadDressRenderer());
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FnCEntities.JOCKEY.get(), Jockey.createJockeyAttributes().build());
        event.put(FnCEntities.BOAR.get(), Boar.createAttributes().build());
        event.put(FnCEntities.JACKALOPE.get(), Jackalope.createAttributes().build());
        event.put(FnCEntities.SABERTOOTH.get(), Sabertooth.createAttributes().build());
    }
}
