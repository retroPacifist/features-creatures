package net.examplemod;

import net.examplemod.network.FabricNetworkHandler;
import net.fabricmc.api.ModInitializer;

public class FeaturesAndCreaturesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricNetworkHandler.init();
    }
}
