package retropacifist.featuresandcreatures.platform;

import com.google.auto.service.AutoService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import retropacifist.featuresandcreatures.common.item.FeaturesCreaturesItems;
import retropacifist.featuresandcreatures.network.FnCPacket;
import retropacifist.featuresandcreatures.network.ForgeNetworkHandler;

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

    @Override
    public CreativeModeTab getCreativeTab() {
        return new CreativeModeTab("featuresandcreatures.featuresandcreatures") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(FeaturesCreaturesItems.MEGA_POTION.get());
            }

            @Override
            public boolean hasSearchBar() {
                return true;
            }

            @Override
            public boolean canScroll() {
                return true;
            }

            @Override
            public ResourceLocation getBackgroundImage() {
                return new ResourceLocation("minecraft", "textures/gui/container/creative_inventory/tab_item_search.png");
            }
        };
    }
}