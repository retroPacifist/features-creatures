package net.msrandom.featuresandcreatures.common.items.spear;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SpearModel extends AnimatedGeoModel<SpearItem> {
    @Override
    public ResourceLocation getModelLocation(SpearItem object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/spear.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(SpearItem object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/models/spear.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(SpearItem animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/spear.animation.json");
    }
}
