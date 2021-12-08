package net.msrandom.featuresandcreatures.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

@Mod.EventBusSubscriber(value = {Dist.CLIENT}, modid = FeaturesAndCreatures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FnCRenderEvents {
    @SubscribeEvent
    public static void renderHotbar(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            MatrixStack poseStack = event.getMatrixStack();
            Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
            float f = 0f; // placeholder
            int x = event.getWindow().getGuiScaledWidth() / 2 - 91;
            int y = event.getWindow().getGuiScaledHeight() - 32 + 10;
            AbstractGui.blit(poseStack, x, y, 0, 0f, 84f, 182, 5, 256, 256);
            if (f > 0) {
                AbstractGui.blit(poseStack, x, y, 0, 0f, 89f, (int) (f * 183), 5, 256, 256);
            }
        }
    }
}
