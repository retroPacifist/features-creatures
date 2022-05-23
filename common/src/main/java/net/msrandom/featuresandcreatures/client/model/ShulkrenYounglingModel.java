package net.msrandom.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.entity.ShulkrenYoungling;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createResourceLocation;

public class ShulkrenYounglingModel extends AnimatedGeoModel<ShulkrenYoungling> {
    public static final ResourceLocation TEXTURE = createResourceLocation("textures/entity/shulkren_youngling.png");


    @Override
    public ResourceLocation getModelLocation(ShulkrenYoungling object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/shulkren_youngling.geo.json");
    }

    @Override
    public ResourceLocation _getTextureLocation(ShulkrenYoungling object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ShulkrenYoungling animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/shulkren_youngling.animation.json");
    }
}