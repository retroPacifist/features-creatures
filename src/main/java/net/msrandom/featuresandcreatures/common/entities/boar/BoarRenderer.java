package net.msrandom.featuresandcreatures.common.entities.boar;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BoarRenderer<T extends Boar> extends GeoEntityRenderer<T> {
    public static final ResourceLocation MADTEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/boarAngry.png");
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/boar.png");

    public BoarRenderer(EntityRendererManager dispatcher) {
        super(dispatcher, new BoarModel());
        this.shadowRadius = 0.2F;
    }

    public static ResourceLocation texture(Boar boar) {
        if (boar.getTarget() != null) {
            if (boar.getTarget() instanceof PlayerEntity) {
                return MADTEXTURE;
            }
        }
            return TEXTURE;
    }
    @Override
    public ResourceLocation getTextureLocation(Boar boar) {
        return texture(boar);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entity.isBaby()){
            stack.scale(0.5f, 0.5f, 0.5f);
        }
        stack.scale(1f, 1f, 1f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }

    @Override
    public boolean shouldRender(T boar, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
        return boar.isAlive();
    }
}
