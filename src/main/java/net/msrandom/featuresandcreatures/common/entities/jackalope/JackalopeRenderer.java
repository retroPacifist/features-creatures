package net.msrandom.featuresandcreatures.common.entities.jackalope;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JackalopeRenderer extends GeoEntityRenderer<Jackalope> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/jackalope.png");

    public JackalopeRenderer(EntityRendererManager dispatcher) {
        super(dispatcher, new JackalopeModel());
        this.shadowRadius = 0.2F;
    }

    @Override
    public ResourceLocation getTextureLocation(Jackalope instance) {
        return TEXTURE;
    }

    @Override
    public void render(Jackalope entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entity.isBaby()){
            stack.scale(0.5f, 0.5f, 0.5f);
        }
        stack.scale(1f, 1f, 1f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}
