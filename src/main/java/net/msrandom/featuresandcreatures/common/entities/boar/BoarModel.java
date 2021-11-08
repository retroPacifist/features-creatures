package net.msrandom.featuresandcreatures.common.entities.boar;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.entities.jackalope.JackalopeRenderer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BoarModel<T extends IAnimatable> extends AnimatedGeoModel<T> {

    @Override
    public ResourceLocation getModelLocation(T object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/boar.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(T object) {
        Boar boar = (Boar) object;
        return BoarRenderer.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/boar.animation.json");

    }
}