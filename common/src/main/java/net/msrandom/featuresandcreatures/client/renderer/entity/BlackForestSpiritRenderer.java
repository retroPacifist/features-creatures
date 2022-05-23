package net.msrandom.featuresandcreatures.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.client.model.BlackForestSpiritModel;
import net.msrandom.featuresandcreatures.client.renderer.entity.layers.BFSEyeLayer;
import net.msrandom.featuresandcreatures.common.entity.BlackForestSpirit;
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
