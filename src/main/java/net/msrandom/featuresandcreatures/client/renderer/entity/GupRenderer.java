package net.msrandom.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.client.model.GupModel;
import net.msrandom.featuresandcreatures.entity.Gup;
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
        stack.translate(0.0D, 0.001F, 0.0D);

        switch (entity.getSize()) {
            case 1 -> {
                float f1 = (float) entity.getSize() / 1.01F;
                stack.scale(f1, f1, f1);
            }
            case 2 -> {
                float f2 = (float) entity.getSize() / 1.433333F;
                stack.scale(f2, f2, f2);
            }
            case 3 -> {
                float f3 = (float) entity.getSize() / 1.75F;
                stack.scale(f3, f3, f3);
            }
        }
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}
