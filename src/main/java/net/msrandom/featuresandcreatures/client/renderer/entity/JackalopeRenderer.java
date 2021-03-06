package net.msrandom.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.entity.Jackalope;
import net.msrandom.featuresandcreatures.client.model.JackalopeModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JackalopeRenderer extends GeoEntityRenderer<Jackalope> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/jackalope.png");
    public static final ResourceLocation SADDLED = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/jackalope_saddle.png");


    public JackalopeRenderer(EntityRendererManager dispatcher) {
        super(dispatcher, new JackalopeModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Jackalope instance) {
        return instance.isSaddled() ? SADDLED : TEXTURE;
    }

    @Override
    public void render(Jackalope entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entity.isBaby()) {
            stack.scale(0.5f, 0.5f, 0.5f);
            shadowRadius = 0.5f;
        } else {
            shadowRadius = 0.8f;
        }

        if(entity.getLeashHolder() != null)
            AbstractMountRenderer.renderLeash(entity, partialTicks, stack, bufferIn, entity.getLeashHolder());

        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}
