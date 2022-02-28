package net.msrandom.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.client.model.SabertoothModel;
import net.msrandom.featuresandcreatures.entity.mount.Sabertooth;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class SabertoothRenderer extends GeoEntityRenderer<Sabertooth> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/sabertooth.png");
    public static final ResourceLocation SADDLED = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/sabertooth_saddle.png");


    public SabertoothRenderer(EntityRendererProvider.Context context) {
        super(context, new SabertoothModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Sabertooth object) {
        return object.isSaddled() ? SabertoothRenderer.SADDLED : SabertoothRenderer.TEXTURE;
    }

    @Override
    public void render(Sabertooth entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        shadowRadius = 0.7f;
        if (entity.isBaby()) {
            stack.scale(0.5f, 0.5f, 0.5f);
            shadowRadius *= 2;
        }

        if(entity.getLeashHolder() != null)
            AbstractMountRenderer.renderLeash(entity, partialTicks, stack, bufferIn, entity.getLeashHolder());

        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}