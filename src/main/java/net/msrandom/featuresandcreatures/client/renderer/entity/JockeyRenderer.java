package net.msrandom.featuresandcreatures.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.client.renderer.entity.model.JockeyModel;
import net.msrandom.featuresandcreatures.entity.JockeyEntity;

public class JockeyRenderer extends MobRenderer<JockeyEntity, JockeyModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/entity/jockey.png");

    public JockeyRenderer(EntityRendererManager dispatcher) {
        super(dispatcher, new JockeyModel(), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(JockeyEntity p_110775_1_) {
        return TEXTURE;
    }
}
