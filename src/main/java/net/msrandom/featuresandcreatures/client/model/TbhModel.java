package net.msrandom.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.entity.Tbh;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createResourceLocation;

public class TbhModel extends AnimatedGeoModel<Tbh> {
    public static final ResourceLocation TEXTURE = createResourceLocation("textures/entity/tbh.png");


    @Override
    public ResourceLocation getModelLocation(Tbh object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/tbh.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Tbh object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Tbh animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/tbh.animation.json");
    }
}