package net.msrandom.featuresandcreatures;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
import net.msrandom.featuresandcreatures.entity.Sabertooth;
import net.msrandom.featuresandcreatures.entity.mount.Boar;
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

    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(FnCEntities.JOCKEY, JockeyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnCEntities.BOAR, BoarEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnCEntities.JACKALOPE, JackalopeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnCEntities.SABERTOOTH, SabertoothRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FnCEntities.SPEAR, SpearRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(AntlerHeaddressItem.class, new AntlerHeaddressRenderer());

        BuiltInGuiTextureRenderer.register(FnCItems.SPEAR);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FnCEntities.JOCKEY, Jockey.createJockeyAttributes().build());
        event.put(FnCEntities.BOAR, Boar.createBoarAttributes().build());
        event.put(FnCEntities.JACKALOPE, Jackalope.createAttributes().build());
        event.put(FnCEntities.SABERTOOTH, Sabertooth.createAttributes().build());
    }

    public static <T extends Entity> @Nullable T createEntity(EntityType<T> entityType, World world, Consumer<T> consumer) {
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
