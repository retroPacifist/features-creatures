package net.msrandom.featuresandcreatures.common.entities.boar;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;


public class BoarRenderer extends GeoEntityRenderer<Boar> {
    public static final ResourceLocation MADTEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/boar_angry.png");
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/boar.png");
    public static final ResourceLocation SADDLED = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/boar_saddle.png");


    public BoarRenderer(EntityRendererManager dispatcher) {
        super(dispatcher, new BoarModel());
    }

    public static ResourceLocation texture(Boar object) {
        if (object.isSaddled()) {
            return SADDLED;
        } else if (object.isAngry()) {
            return MADTEXTURE;
        } else return TEXTURE;
    }

    @Override
    public ResourceLocation getTextureLocation(Boar boar) {
        return texture(boar);
    }

    @Override
    public void render(Boar entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entity.isBaby()) {
            stack.scale(0.5f, 0.5f, 0.5f);
            shadowRadius = 0.7f;
        } else {
            shadowRadius = 1.1f;
        }
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}