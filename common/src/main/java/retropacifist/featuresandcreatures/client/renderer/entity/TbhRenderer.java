package retropacifist.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.client.model.TbhModel;
import retropacifist.featuresandcreatures.common.entity.Tbh;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class TbhRenderer extends GeoEntityRenderer<Tbh> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/tbh.png");

    public TbhRenderer(EntityRendererProvider.Context dispatcher) {
        super(dispatcher, new TbhModel());
    }

    @Override
    public void render(Tbh entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        shadowRadius = 0.3f;
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation _getTextureLocation(Tbh entity) {
        return TEXTURE;
    }
}