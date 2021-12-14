package net.msrandom.featuresandcreatures;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.msrandom.featuresandcreatures.client.BuiltInGuiTextureRenderer;
import net.msrandom.featuresandcreatures.client.renderer.entity.*;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCItems;
import net.msrandom.featuresandcreatures.core.FnCKeybinds;
import net.msrandom.featuresandcreatures.core.FnCTriggers;
import net.msrandom.featuresandcreatures.entity.Boar;
import net.msrandom.featuresandcreatures.entity.Jackalope;
import net.msrandom.featuresandcreatures.entity.Jockey;
import net.msrandom.featuresandcreatures.entity.Sabertooth;
import net.msrandom.featuresandcreatures.item.AntlerHeaddressItem;
import net.msrandom.featuresandcreatures.item.AntlerHeaddressRenderer;
import net.msrandom.featuresandcreatures.network.NetworkHandler;
import net.msrandom.featuresandcreatures.util.FnCConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mod(FeaturesAndCreatures.MOD_ID)
public class FeaturesAndCreatures {
    private static final String NETWORK_VERSION = "1";
    public static final String MOD_ID = "featuresandcreatures";
    public static final Logger LOGGER = LogManager.getLogger();

    public FeaturesAndCreatures() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::registerAttributes);
        FnCEntities.REGISTRAR.register(bus);
        FnCItems.REGISTRAR.register(bus);
        FnCTriggers.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, FnCConfig.getConfigSpec());

        //GeckoLib
        GeckoLib.initialize();

    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.init();
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
