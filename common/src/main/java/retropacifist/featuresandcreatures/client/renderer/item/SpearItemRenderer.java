package retropacifist.featuresandcreatures.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import retropacifist.featuresandcreatures.client.renderer.entity.SpearRenderer;

public class SpearItemRenderer extends BlockEntityWithoutLevelRenderer {
    public SpearItemRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource bufferProvider, int packed, int combinedOverlay) {
        poseStack.pushPose();
        VertexConsumer vertexBuilder = ItemRenderer.getFoilBufferDirect(bufferProvider, SpearRenderer.MODEL.renderType(SpearRenderer.TEXTURE), true, stack.hasFoil());
        SpearRenderer.MODEL.renderToBuffer(poseStack, vertexBuilder, packed, combinedOverlay, 1, 1, 1, 1);
        poseStack.popPose();
    }
}
