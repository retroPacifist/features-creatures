package net.msrandom.featuresandcreatures.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.client.model.GupModel;
import net.msrandom.featuresandcreatures.entity.Gup;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GupRenderer extends GeoEntityRenderer<Gup> {

    public GupRenderer(EntityRendererProvider.Context dispatcher) {
        super(dispatcher, new GupModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(Gup p_110775_1_) {
        return GupModel.TEXTURE;
    }

}
