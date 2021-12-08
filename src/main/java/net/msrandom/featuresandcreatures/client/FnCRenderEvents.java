package net.msrandom.featuresandcreatures.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.item.AntlerHeaddressItem;

@Mod.EventBusSubscriber(value = {Dist.CLIENT}, modid = FeaturesAndCreatures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FnCRenderEvents {
    @SubscribeEvent
    public static void renderHotbar(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            Minecraft client = Minecraft.getInstance();
            ClientPlayerEntity player = client.player;
            if (player != null) {
                Item helmet = player.getItemBySlot(EquipmentSlotType.HEAD).getItem();
                if (helmet instanceof AntlerHeaddressItem) {
                    float percentage = player.getPersistentData().getInt(AntlerHeaddressItem.CURRENT_CHARGE) / (float) ((AntlerHeaddressItem) helmet).getMaxCharge();
                    if (percentage > 0) {
                        int width = 182;
                        int x = event.getWindow().getGuiScaledWidth() / 2 - width / 2;
                        int y = event.getWindow().getGuiScaledHeight() - 29;
                        MatrixStack poseStack = event.getMatrixStack();
                        client.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
                        RenderSystem.disableBlend();
                        AbstractGui.blit(poseStack, x, y, 0, 0f, 84f, width, 5, 256, 256);
                        AbstractGui.blit(poseStack, x, y, 0, 0f, 89f, (int) (percentage * (width + 1)), 5, 256, 256);
                        RenderSystem.enableBlend();
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
