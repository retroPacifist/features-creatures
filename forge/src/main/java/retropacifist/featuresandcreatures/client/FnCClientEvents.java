package retropacifist.featuresandcreatures.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.item.AntlerHeaddressItem;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = {Dist.CLIENT}, modid = FeaturesAndCreatures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FnCClientEvents {
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
}
