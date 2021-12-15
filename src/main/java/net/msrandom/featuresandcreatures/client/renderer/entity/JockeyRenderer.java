package net.msrandom.featuresandcreatures.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.entity.Jockey;
import net.msrandom.featuresandcreatures.client.model.JockeyModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JockeyRenderer extends GeoEntityRenderer<Jockey> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/jockey.png");

    public JockeyRenderer(EntityRendererManager dispatcher) {
        super(dispatcher, new JockeyModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(Jockey p_110775_1_) {
        return TEXTURE;
    }

}
