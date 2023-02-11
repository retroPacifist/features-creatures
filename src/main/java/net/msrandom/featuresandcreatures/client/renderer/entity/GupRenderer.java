package net.msrandom.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.client.model.GupModel;
import net.msrandom.featuresandcreatures.common.entity.Gup;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GupRenderer extends GeoEntityRenderer<Gup> {

    public GupRenderer(EntityRendererProvider.Context dispatcher) {
        super(dispatcher, new GupModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(Gup p_110775_1_) {
        return GupModel.TEXTURE;
    }

    @Override
    public void render(Gup entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {

        float gupModelBlockHeight = 1.75f;
        float scalingFactor = 1 / gupModelBlockHeight;
        switch (entity.getSize()) {
            case 2 -> {
                scalingFactor *= 2.5f;
            }
            case 3 -> {
                scalingFactor *= 5.0f;
            }
        }
        stack.scale(scalingFactor, scalingFactor, scalingFactor);

        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}
