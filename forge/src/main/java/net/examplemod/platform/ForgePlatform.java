package net.examplemod.platform;

import com.google.auto.service.AutoService;
import net.examplemod.network.ForgeNetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.msrandom.featuresandcreatures.network.FnCPacket;
import net.msrandom.featuresandcreatures.platform.ModPlatform;

import java.nio.file.Path;

@AutoService(ModPlatform.class)
public class ForgePlatform implements ModPlatform {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public <P extends FnCPacket> void sendToClient(ServerPlayer player, P packet) {
        ForgeNetworkHandler.sendToPlayer(player, packet);
    }

    @Override
    public <P extends FnCPacket> void sendToServer(P packet) {
        ForgeNetworkHandler.sendToServer(packet);
    }

    @Override
    public Path configPath() {
        return FMLPaths.CONFIGDIR.get();
    }
}