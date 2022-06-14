package net.examplemod.forge;

import net.examplemod.network.ForgeNetworkHandler;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.client.BuiltInGuiTextureRenderer;
import net.msrandom.featuresandcreatures.client.model.SpearModel;
import net.msrandom.featuresandcreatures.client.renderer.entity.*;
import net.msrandom.featuresandcreatures.common.entity.*;
import net.msrandom.featuresandcreatures.common.entity.mount.Boar;
import net.msrandom.featuresandcreatures.common.entity.mount.Jackalope;
import net.msrandom.featuresandcreatures.common.entity.mount.Sabertooth;
import net.msrandom.featuresandcreatures.common.item.*;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCKeybinds;
import net.msrandom.featuresandcreatures.core.FnCRegistry;
import net.msrandom.featuresandcreatures.core.FnCTriggers;
import net.msrandom.featuresandcreatures.util.FnCConfig;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;


@Mod(FeaturesAndCreatures.MOD_ID)
public class FeaturesAndCreaturesForge {
    public FeaturesAndCreaturesForge() {
        FnCRegistry.loadClasses();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::registerAttributes);
        bus.addListener(this::registerModels);
        bus.addListener(this::registerRenderers);
        bus.addListener(this::registerArmor);
        bus.addListener(this::bakeLayers);

        FnCTriggers.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, FnCConfig.getConfigSpec());

        //GeckoLib
        GeckoLib.initialize();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ForgeNetworkHandler.init();
        FnCEntities.registerSpawnPlacements();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        FnCKeybinds.register().forEach(ClientRegistry::registerKeyBinding);
    }

    public void registerRenderers(EntityRenderersEvent.RegisterRenderers renderer) {
        renderer.registerEntityRenderer(FnCEntities.JOCKEY.get(), JockeyRenderer::new);
        renderer.registerEntityRenderer(FnCEntities.BOAR.get(), BoarEntityRenderer::new);
        renderer.registerEntityRenderer(FnCEntities.JACKALOPE.get(), JackalopeRenderer::new);
        renderer.registerEntityRenderer(FnCEntities.SABERTOOTH.get(), SabertoothRenderer::new);
        renderer.registerEntityRenderer(FnCEntities.SPEAR.get(), SpearRenderer::new);
        renderer.registerEntityRenderer(FnCEntities.BLACK_FOREST_SPIRIT.get(), BlackForestSpiritRenderer::new);
        renderer.registerEntityRenderer(FnCEntities.GUP.get(), GupRenderer::new);
        renderer.registerEntityRenderer(FnCEntities.BRIMSTONE_GOLEM.get(), BrimstoneGolemRenderer::new);
        renderer.registerEntityRenderer(FnCEntities.SHULKREN_YOUNGLING.get(), ShulkrenYounglingRenderer::new);
        renderer.registerEntityRenderer(FnCEntities.BFS_ATTACK.get(), BFSAttackRenderer::new);
        renderer.registerEntityRenderer(FnCEntities.TBH.get(), TbhRenderer::new);

        ItemProperties.register(
                FeaturesCreaturesItems.SPEAR.get(),
                new ResourceLocation(FeaturesAndCreatures.MOD_ID, "throwing"),
                (stack, world, entity, level) -> {
                    return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                });
    }

    public void bakeLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpearModel.LAYER_LOCATION, SpearModel::createBodyLayer);
    }

    public void registerArmor(final EntityRenderersEvent.AddLayers event) {
        GeoArmorRenderer.registerArmorRenderer(FeaturesCreaturesItems.ANTLER_HEADDRESS.get().getClass(), new AntlerHeaddressRenderer());
        GeoArmorRenderer.registerArmorRenderer(FeaturesCreaturesItems.LUNAR_HEADDRESS.get().getClass(), new LunarHeaddressRenderer());

    }

    private void registerModels(ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(BuiltInGuiTextureRenderer.register(FeaturesCreaturesItems.SPEAR.get()));
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FnCEntities.JOCKEY.get(), Jockey.createJockeyAttributes().build());
        event.put(FnCEntities.BOAR.get(), Boar.createBoarAttributes().build());
        event.put(FnCEntities.JACKALOPE.get(), Jackalope.createAttributes().build());
        event.put(FnCEntities.SABERTOOTH.get(), Sabertooth.createSabertoothAttributes().build());
        event.put(FnCEntities.BLACK_FOREST_SPIRIT.get(), BlackForestSpirit.createAttributes().build());
        event.put(FnCEntities.GUP.get(), Gup.createAttributes().build());
        event.put(FnCEntities.BRIMSTONE_GOLEM.get(), BrimstoneGolem.createAttributes().build());
        event.put(FnCEntities.SHULKREN_YOUNGLING.get(), ShulkrenYoungling.createAttributes().build());
        event.put(FnCEntities.TBH.get(), Tbh.createAttributes().build());

    }

}
