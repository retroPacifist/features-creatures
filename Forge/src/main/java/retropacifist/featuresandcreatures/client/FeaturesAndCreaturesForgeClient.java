package retropacifist.featuresandcreatures.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.client.model.SpearModel;
import retropacifist.featuresandcreatures.client.renderer.BuiltInGuiTextureRenderer;
import retropacifist.featuresandcreatures.client.renderer.item.AntlerHeaddressRenderer;
import retropacifist.featuresandcreatures.client.renderer.item.LunarHeaddressRenderer;
import retropacifist.featuresandcreatures.common.item.AntlerHeaddressItem;
import retropacifist.featuresandcreatures.common.item.FeaturesCreaturesItems;
import retropacifist.featuresandcreatures.common.item.LunarHeaddressItem;
import retropacifist.featuresandcreatures.core.FnCKeybinds;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FeaturesAndCreaturesForgeClient {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers renderer) {
        FnCEntityRendererProvider.INSTANCE.getRenderers().forEach((entityEntityType, entityRendererProvider) -> add(renderer, entityEntityType, entityRendererProvider));

        ItemProperties.register(
                FeaturesCreaturesItems.SPEAR.get(),
                new ResourceLocation(FeaturesAndCreatures.MOD_ID, "throwing"),
                (stack, world, entity, level) -> {
                    return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                });
    }

    @SubscribeEvent
    public void bakeLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpearModel.LAYER_LOCATION, SpearModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerArmor(final EntityRenderersEvent.AddLayers event) {
        GeoArmorRenderer.registerArmorRenderer(AntlerHeaddressItem.class, new AntlerHeaddressRenderer());
        GeoArmorRenderer.registerArmorRenderer(LunarHeaddressItem.class, new LunarHeaddressRenderer());

    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        BuiltInGuiTextureRenderer.register(FeaturesCreaturesItems.SPEAR.get());
    }

    @SubscribeEvent
    public static void renderHotbar(RenderGameOverlayEvent.Pre event) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player != null) {
            Item helmet = player.getItemBySlot(EquipmentSlot.HEAD).getItem();
            if (helmet instanceof AntlerHeaddressItem) {
                float percentage = player.getPersistentData().getInt(AntlerHeaddressItem.CURRENT_CHARGE) / (float) ((AntlerHeaddressItem) helmet).getMaxCharge();
                if (percentage > 0) {
                    int width = 182;
                    int x = event.getWindow().getGuiScaledWidth() / 2 - width / 2;
                    int y = event.getWindow().getGuiScaledHeight() - 29;
                    PoseStack poseStack = event.getMatrixStack();
                    RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
                    RenderSystem.disableBlend();
                    GuiComponent.blit(poseStack, x, y, 0, 0f, 84f, width, 5, 256, 256);
                    GuiComponent.blit(poseStack, x, y, 0, 0f, 89f, (int) (percentage * (width + 1)), 5, 256, 256);
                    RenderSystem.enableBlend();
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void getTooltips(ItemTooltipEvent event) {
        // Hacky workaround for vanilla potion duration bug. May break if mods add any tooltips before index 1
        // -Potential solution to that would be figuring out the proper start index by comparing the would-be tooltip.
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() == Items.LINGERING_POTION || itemStack.getItem() == Items.TIPPED_ARROW) {
            List<Component> newList = new ArrayList<>();
            PotionUtils.addPotionTooltip(itemStack, newList, 1f);
            List<Component> list = event.getToolTip();
            for (int i = 0; i < newList.size(); ++i) {
                list.set(i + 1, newList.get(i));
            }
        }
    }


    public static void client() {
        FnCKeybinds.register();
    }

    private static <T extends Entity> void add(EntityRenderersEvent.RegisterRenderers event, EntityType<T> entityType, EntityRendererProvider<T> provider) {
        event.registerEntityRenderer(entityType, provider);
    }
}