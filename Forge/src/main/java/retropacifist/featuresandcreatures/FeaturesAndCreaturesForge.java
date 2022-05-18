package retropacifist.featuresandcreatures;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import retropacifist.featuresandcreatures.client.FeaturesAndCreaturesForgeClient;
import retropacifist.featuresandcreatures.common.entity.*;
import retropacifist.featuresandcreatures.common.entity.mount.Boar;
import retropacifist.featuresandcreatures.common.entity.mount.Jackalope;
import retropacifist.featuresandcreatures.common.entity.mount.Sabertooth;
import retropacifist.featuresandcreatures.core.FnCEntities;
import retropacifist.featuresandcreatures.core.FnCRegistries;
import retropacifist.featuresandcreatures.core.FnCTriggers;
import retropacifist.featuresandcreatures.network.ForgeNetworkHandler;
import retropacifist.featuresandcreatures.util.FnCConfig;
import software.bernie.geckolib3.GeckoLib;

@Mod(FeaturesAndCreatures.MOD_ID)
public class FeaturesAndCreaturesForge {


    public FeaturesAndCreaturesForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        FnCRegistries.bootStrap();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::registerAttributes);
        FnCTriggers.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, FnCConfig.getConfigSpec());

        //GeckoLib
        GeckoLib.initialize();
    }


    private void clientSetup(FMLClientSetupEvent event) {
        FeaturesAndCreaturesForgeClient.client();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ForgeNetworkHandler.init();
        FnCEntities.registerSpawnPlacements();
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FnCEntities.JOCKEY.get(), Jockey.createJockeyAttributes().build());
        event.put(FnCEntities.BOAR.get(), Boar.createBoarAttributes().build());
        event.put(FnCEntities.JACKALOPE.get(), Jackalope.createAttributes().build());
        event.put(FnCEntities.SABERTOOTH.get(), Sabertooth.createSabertoothAttributes().build());
        event.put(FnCEntities.BLACK_FOREST_SPIRIT.get(), BlackForestSpiritImpl.createAttributes().build());
        event.put(FnCEntities.GUP.get(), Gup.createAttributes().build());
        event.put(FnCEntities.BRIMSTONE_GOLEM.get(), BrimstoneGolem.createAttributes().build());
        event.put(FnCEntities.SHULKREN_YOUNGLING.get(), ShulkrenYounglingImpl.createAttributes().build());
        event.put(FnCEntities.TBH.get(), Tbh.createAttributes().build());
    }
}