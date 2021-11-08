package net.msrandom.featuresandcreatures.common.entities.sabertooth;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class SabertoothRenderer extends GeoEntityRenderer<Sabertooth> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/sabertooth.png");

    public SabertoothRenderer(EntityRendererManager dispatcher) {
        super(dispatcher, new SabertoothModel());
        this.shadowRadius = 0.2F;
    }

    @Override
    public ResourceLocation getTextureLocation(net.msrandom.featuresandcreatures.common.entities.sabertooth.Sabertooth p_110775_1_) {
        return TEXTURE;
    }

    @Override
    public void render(Sabertooth entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entity.isBaby()){
            stack.scale(0.5f, 0.5f, 0.5f);
        }
        stack.scale(1f, 1f, 1f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}
