package retropacifist.featuresandcreatures.platform;

import com.google.auto.service.AutoService;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.item.FeaturesCreaturesItems;
import retropacifist.featuresandcreatures.network.FabricNetworkHandler;
import retropacifist.featuresandcreatures.network.FnCPacket;

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

    @Override
    public CreativeModeTab getCreativeTab() {
        return FabricItemGroupBuilder.build(FeaturesAndCreatures.createResourceLocation(FeaturesAndCreatures.MOD_ID), () -> new ItemStack(FeaturesCreaturesItems.MEGA_POTION.get()));
    }
}
