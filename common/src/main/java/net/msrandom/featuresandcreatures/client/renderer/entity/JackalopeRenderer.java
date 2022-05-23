package net.msrandom.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.client.model.JackalopeModel;
import net.msrandom.featuresandcreatures.common.entity.mount.Jackalope;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JackalopeRenderer extends GeoEntityRenderer<Jackalope> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/jackalope.png");
    public static final ResourceLocation SADDLED = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/jackalope_saddle.png");


    public JackalopeRenderer(EntityRendererProvider.Context dispatcher) {
        super(dispatcher, new JackalopeModel());
    }

    @Override
    public ResourceLocation _getTextureLocation(Jackalope instance) {
        return instance.isSaddled() ? SADDLED : TEXTURE;
    }

    @Override
    public void render(Jackalope entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        if (entity.isBaby()) {
            stack.scale(0.5f, 0.5f, 0.5f);
            shadowRadius = 0.5f;
        } else {
            shadowRadius = 0.8f;
        }
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }

}
