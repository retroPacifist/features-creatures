package net.msrandom.featuresandcreatures.common.entities.jockey;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
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
