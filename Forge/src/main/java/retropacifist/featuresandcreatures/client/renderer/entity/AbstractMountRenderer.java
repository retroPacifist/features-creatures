package retropacifist.featuresandcreatures.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import retropacifist.featuresandcreatures.common.entity.mount.AbstractMountEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class AbstractMountRenderer<T extends AbstractMountEntity> extends GeoEntityRenderer<T> {
    private final float defaultShadowRadius;

    protected AbstractMountRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider, float defaultShadowRadius) {
        super(renderManager, modelProvider);
        this.defaultShadowRadius = defaultShadowRadius;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        if (entity.isBaby()) {
            float scale = 0.5F;
            stack.scale(scale, scale, scale);
            shadowRadius = defaultShadowRadius * 0.75F;
        } else {
            shadowRadius = defaultShadowRadius;
        }
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}
