package net.examplemod.platform;

import com.google.auto.service.AutoService;
import net.examplemod.network.FabricNetworkHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.network.FnCPacket;
import net.msrandom.featuresandcreatures.platform.ModPlatform;

import java.nio.file.Path;

@AutoService(ModPlatform.class)
public class FabricModPlatform implements ModPlatform {

    @Override
    public Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FeaturesAndCreatures.MOD_ID);
    }

    @Override
    public boolean isModLoaded(String isLoaded) {
        return FabricLoader.getInstance().isModLoaded(isLoaded);
    }

    @Override
    public <P extends FnCPacket> void sendToClient(ServerPlayer player, P packet) {
        FabricNetworkHandler.sendToPlayer(player, packet);
    }

    @Override
    public <P extends FnCPacket> void sendToServer(P packet) {
        FabricNetworkHandler.sendToServer(packet);
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
