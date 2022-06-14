package retropacifist.featuresandcreatures;

import net.fabricmc.api.ModInitializer;
import retropacifist.featuresandcreatures.network.FabricNetworkHandler;

public class FeaturesAndCreaturesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricNetworkHandler.init();
    }
}
