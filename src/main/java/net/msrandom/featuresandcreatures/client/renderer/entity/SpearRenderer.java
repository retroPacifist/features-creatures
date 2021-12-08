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
    public static final ResourceLocation SPEAR_TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/spear_entity.png");
    private final SpearModel spearModel = new SpearModel();

    public SpearRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(Spear entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) - 90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot) + 90.0F));
        IVertexBuilder ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferIn, this.spearModel.renderType(this.getTextureLocation(entityIn)), false, false);
        this.spearModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public ResourceLocation getTextureLocation(Spear entity) {
        return SPEAR_TEXTURE;
    }
}

