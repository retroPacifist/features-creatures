package net.msrandom.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.entity.BlackForestSpirit;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createResourceLocation;

public class BlackForestSpiritModel extends AnimatedGeoModel<BlackForestSpirit> {
    public static final ResourceLocation TEXTURE = createResourceLocation("textures/entity/spirit/black_forest_spirit.png");


    @Override
    public ResourceLocation getModelLocation(BlackForestSpirit object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/black_forest_spirit.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(BlackForestSpirit object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BlackForestSpirit animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/black_forest_spirit.animation.json");
    }
}