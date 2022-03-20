package net.msrandom.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.client.model.BrimstoneGolemModel;
import net.msrandom.featuresandcreatures.entity.BrimstoneGolem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BrimstoneGolemRenderer extends GeoEntityRenderer<BrimstoneGolem> {


    public BrimstoneGolemRenderer(EntityRendererProvider.Context dispatcher) {
        super(dispatcher, new BrimstoneGolemModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(BrimstoneGolem p_110775_1_) {
        return BrimstoneGolemModel.TEXTURE;
    }

    @Override
    public RenderType getRenderType(BrimstoneGolem animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(BrimstoneGolemModel.TEXTURE);
    }
}
