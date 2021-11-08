package net.msrandom.featuresandcreatures.common.entities.boar;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.entities.jackalope.JackalopeRenderer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BoarModel extends AnimatedGeoModel<Boar> {

    @Override
    public ResourceLocation getModelLocation(Boar object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/boar.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Boar object) {
        return BoarRenderer.texture(object);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Boar animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/boar.animation.json");

    }
}