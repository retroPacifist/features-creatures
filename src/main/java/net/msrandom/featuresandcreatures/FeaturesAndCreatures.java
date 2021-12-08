package net.msrandom.featuresandcreatures;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.msrandom.featuresandcreatures.common.entities.boar.Boar;
import net.msrandom.featuresandcreatures.common.entities.boar.BoarRenderer;
import net.msrandom.featuresandcreatures.common.entities.jackalope.Jackalope;
import net.msrandom.featuresandcreatures.common.entities.jackalope.JackalopeRenderer;
import net.msrandom.featuresandcreatures.common.entities.jockey.Jockey;
import net.msrandom.featuresandcreatures.common.entities.jockey.JockeyRenderer;
import net.msrandom.featuresandcreatures.common.entities.sabertooth.Sabertooth;
import net.msrandom.featuresandcreatures.common.entities.sabertooth.SabertoothRenderer;
import net.msrandom.featuresandcreatures.common.entities.spear.SpearRenderer;
import net.msrandom.featuresandcreatures.common.items.antler_headdress.AntlerHeaddressItem;
import net.msrandom.featuresandcreatures.common.items.antler_headdress.AntlerHeaddressRenderer;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCItems;
import net.msrandom.featuresandcreatures.core.FnCKeybinds;
import net.msrandom.featuresandcreatures.core.FnCTriggers;
import net.msrandom.featuresandcreatures.network.AntlerHeaddressChargePacket;
import net.msrandom.featuresandcreatures.util.BuiltInGuiTextureRenderer;
import net.msrandom.featuresandcreatures.util.WorldJockeyCapability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mod(FeaturesAndCreatures.MOD_ID)
public class FeaturesAndCreatures {
    private static final String NETWORK_VERSION = "1";
    public static final String MOD_ID = "featuresandcreatures";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "network"),
            () -> NETWORK_VERSION,
            NETWORK_VERSION::equals,
            NETWORK_VERSION::equals
    );

    public FeaturesAndCreatures() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::registerAttributes);
        FnCEntities.REGISTRAR.register(bus);
        FnCItems.REGISTRAR.register(bus);
        FnCTriggers.register();

        //GeckoLib
        GeckoLib.initialize();

        AntlerHeaddressChargePacket.register(0, NETWORK_CHANNEL);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(WorldJockeyCapability.class, new WorldJockeyCapability.Storage(), () -> new WorldJockeyCapability(null));
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
        GeoArmorRenderer.registerArmorRenderer(AntlerHeaddressItem.class, new AntlerHeaddressRenderer());

        BuiltInGuiTextureRenderer.register(FnCItems.SPEAR.get());
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FnCEntities.JOCKEY.get(), Jockey.createJockeyAttributes().build());
        event.put(FnCEntities.BOAR.get(), Boar.createAttributes().build());
        event.put(FnCEntities.JACKALOPE.get(), Jackalope.createAttributes().build());
        event.put(FnCEntities.SABERTOOTH.get(), Sabertooth.createAttributes().build());
    }
}
