package net.msrandom.featuresandcreatures;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
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
import net.msrandom.featuresandcreatures.core.*;
import net.msrandom.featuresandcreatures.entity.Jackalope;
import net.msrandom.featuresandcreatures.entity.Jockey;
import net.msrandom.featuresandcreatures.entity.mount.Boar;
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
        FnCEntities.REGISTRAR.initialize();
        FnCItems.REGISTRAR.initialize();
        FnCSounds.REGISTRAR.initialize();
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

    @OnlyIn(Dist.CLIENT)
    public void registerRenderers() {
        EntityRenderers.register(FnCEntities.JOCKEY, JockeyRenderer::new);
        EntityRenderers.register(FnCEntities.BOAR, BoarEntityRenderer::new);
        EntityRenderers.register(FnCEntities.JACKALOPE, JackalopeRenderer::new);
        EntityRenderers.register(FnCEntities.SABERTOOTH, SabertoothRenderer::new);
        EntityRenderers.register(FnCEntities.SPEAR, SpearRenderer::new);

        GeoArmorRenderer.registerArmorRenderer(AntlerHeaddressItem.class, new AntlerHeaddressRenderer());

        ItemProperties.register(
                FnCItems.SPEAR,
                new ResourceLocation(MOD_ID, "throwing"),
                (stack, level, entity, i) -> entity != null && entity.getUseItem() == stack ? 1f : 0f
        );
    }

    private void registerModels(ModelRegistryEvent event) {
        BuiltInGuiTextureRenderer.register(FnCItems.SPEAR);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FnCEntities.JOCKEY, Jockey.createJockeyAttributes().build());
        event.put(FnCEntities.BOAR, Boar.createBoarAttributes().build());
        event.put(FnCEntities.JACKALOPE, Jackalope.createAttributes().build());
        event.put(FnCEntities.SABERTOOTH, Sabertooth.createSabertoothAttributes().build());
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
