package retropacifist.featuresandcreatures.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import retropacifist.featuresandcreatures.client.model.BlackForestSpiritModel;
import retropacifist.featuresandcreatures.common.entity.BlackForestSpirit;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BlackForestSpiritRenderer extends GeoEntityRenderer<BlackForestSpirit> {

    public BlackForestSpiritRenderer(EntityRendererProvider.Context dispatcher) {
        super(dispatcher, new BlackForestSpiritModel());
        this.addLayer(new BFSEyeLayer(this));
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation _getTextureLocation(BlackForestSpirit spirit) {
        return BlackForestSpiritModel.TEXTURE;
    }

}
