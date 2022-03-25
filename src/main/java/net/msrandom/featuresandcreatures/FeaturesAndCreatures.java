package net.msrandom.featuresandcreatures;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.msrandom.featuresandcreatures.client.BuiltInGuiTextureRenderer;
import net.msrandom.featuresandcreatures.client.model.SpearModel;
import net.msrandom.featuresandcreatures.client.renderer.entity.*;
import net.msrandom.featuresandcreatures.core.*;
import net.msrandom.featuresandcreatures.entity.*;
import net.msrandom.featuresandcreatures.entity.mount.Boar;
import net.msrandom.featuresandcreatures.entity.mount.Jackalope;
import net.msrandom.featuresandcreatures.entity.mount.Sabertooth;
import net.msrandom.featuresandcreatures.item.AntlerHeaddressItem;
import net.msrandom.featuresandcreatures.item.AntlerHeaddressRenderer;
import net.msrandom.featuresandcreatures.network.NetworkHandler;
import net.msrandom.featuresandcreatures.util.FnCConfig;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import java.util.function.Consumer;

@Mod(FeaturesAndCreatures.MOD_ID)
public class FeaturesAndCreatures {
    public static final String MOD_ID = "featuresandcreatures";

    public FeaturesAndCreatures() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::registerAttributes);
        bus.addListener(this::registerModels);
        bus.addListener(this::registerRenderers);
        bus.addListener(this::registerArmor);
        bus.addListener(this::bakeLayers);
        FnCItems.REGISTRAR.register(bus);
        FnCEntities.REGISTRAR.register(bus);
        FnCBlocks.REGISTRAR.register(bus);
        FnCSounds.REGISTRAR.initialize();
        FnCTriggers.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, FnCConfig.getConfigSpec());

        //GeckoLib
        GeckoLib.initialize();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.init();
        SpawnPlacements.register(FnCEntities.BLACK_FOREST_SPIRIT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlackForestSpirit::checkSpawnRules);
        SpawnPlacements.register(FnCEntities.SHULKREN_YOUNGLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ShulkrenYoungling::checkSpawnRules);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        FnCKeybinds.register();
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

        ItemProperties.register(
                FnCItems.SPEAR.get(),
                new ResourceLocation(MOD_ID, "throwing"),
                (stack, world, entity, level) -> {
                    return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                });
    }

    public void bakeLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(SpearModel.LAYER_LOCATION, SpearModel::createBodyLayer);
    }

    public void registerArmor(final EntityRenderersEvent.AddLayers event){
        GeoArmorRenderer.registerArmorRenderer(AntlerHeaddressItem.class, new AntlerHeaddressRenderer());
    }

    private void registerModels(ModelRegistryEvent event) {
        BuiltInGuiTextureRenderer.register(FnCItems.SPEAR.get());
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

    }

    public static <T extends Entity> @Nullable T createEntity(EntityType<T> entityType, Level world, Consumer<T> consumer) {
        T entity = entityType.create(world);
        if (entity != null) {
            consumer.accept(entity);
        }
        return entity;
    }

    public static ResourceLocation createResourceLocation(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
