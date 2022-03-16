package net.msrandom.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.entity.Gup;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createResourceLocation;

public class GupModel extends AnimatedGeoModel<Gup> {
    public static final ResourceLocation TEXTURE = createResourceLocation("textures/entity/gup.png");


    @Override
    public ResourceLocation getModelLocation(Gup object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/gup.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Gup object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Gup animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/gup.animation.json");
    }
}