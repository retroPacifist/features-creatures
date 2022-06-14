package net.msrandom.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.entity.BrimstoneGolem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createResourceLocation;

public class BrimstoneGolemModel extends AnimatedGeoModel<BrimstoneGolem> {
    public static final ResourceLocation TEXTURE = createResourceLocation("textures/entity/brimstone/brimstone_golem.png");


    @Override
    public ResourceLocation getModelLocation(BrimstoneGolem object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/brimstone_golem.geo.json");
    }

    @Override
    public ResourceLocation _getTextureLocation(BrimstoneGolem object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BrimstoneGolem animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/brimstone_golem.animation.json");
    }


}