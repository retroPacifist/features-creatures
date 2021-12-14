package net.msrandom.featuresandcreatures.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.msrandom.featuresandcreatures.client.renderer.entity.SpearRenderer;

public class SpearItemRenderer extends ItemStackTileEntityRenderer {
    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack poseStack, IRenderTypeBuffer bufferProvider, int packed, int combinedOverlay) {
        poseStack.pushPose();
        IVertexBuilder vertexBuilder = ItemRenderer.getFoilBufferDirect(bufferProvider, SpearRenderer.MODEL.renderType(SpearRenderer.TEXTURE), true, stack.hasFoil());
        SpearRenderer.MODEL.renderToBuffer(poseStack, vertexBuilder, packed, combinedOverlay, 1, 1, 1, 1);
        poseStack.popPose();
    }
}
