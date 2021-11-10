package net.msrandom.featuresandcreatures.common.entities.boar;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BoarRenderer extends GeoEntityRenderer<Boar> {
    public static final ResourceLocation MADTEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/boar_angry.png");
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/boar.png");

    public BoarRenderer(EntityRendererManager dispatcher) {
        super(dispatcher, new BoarModel());
        this.shadowRadius = 0.2F;
    }

    public static ResourceLocation texture(Boar boar) {
        return boar.isAngry() ? MADTEXTURE : TEXTURE;
    }

    @Override
    public ResourceLocation getTextureLocation(Boar boar) {
        return texture(boar);
    }

    @Override
    public void render(Boar entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        AnimatedGeoModel<Boar> modelProvider = new BoarModel();
        if (entity.isBaby()) {
            stack.scale(0.5f, 0.5f, 0.5f);
//            IBone head = modelProvider.getAnimationProcessor().getBone("head");
//            head.setScaleX(1.0f);
//            head.setScaleY(1.0f);
//            head.setScaleZ(1.0f);
        }
        stack.scale(1f, 1f, 1f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}
