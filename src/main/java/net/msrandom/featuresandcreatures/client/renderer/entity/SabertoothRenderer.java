package net.msrandom.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.client.model.SabertoothModel;
import net.msrandom.featuresandcreatures.entity.mount.Sabertooth;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class SabertoothRenderer extends GeoEntityRenderer<Sabertooth> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/sabertooth.png");
    public static final ResourceLocation SADDLED = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/sabertooth_saddle.png");


    public SabertoothRenderer(EntityRendererManager dispatcher) {
        super(dispatcher, new SabertoothModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Sabertooth object) {
        return object.isSaddled() ? SabertoothRenderer.SADDLED : SabertoothRenderer.TEXTURE;
    }

    @Override
    public void render(Sabertooth entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        shadowRadius = 0.7f;
        if (entity.isBaby()) {
            stack.scale(0.5f, 0.5f, 0.5f);
            shadowRadius *= 2;
        }
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}