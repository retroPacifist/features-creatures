package net.msrandom.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.client.model.SpearModel;
import net.msrandom.featuresandcreatures.entity.Spear;

public class SpearRenderer extends EntityRenderer<Spear> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/spear_entity.png");
    public static final SpearModel MODEL = new SpearModel();

    public SpearRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(Spear entity, float entityYaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entity.yRotO, entity.yRot) - 90.0F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entity.xRotO, entity.xRot) + 90.0F));
        IVertexBuilder ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferIn, MODEL.renderType(this.getTextureLocation(entity)), false, false);
        MODEL.renderToBuffer(poseStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }

    public ResourceLocation getTextureLocation(Spear entity) {
        return TEXTURE;
    }
}
