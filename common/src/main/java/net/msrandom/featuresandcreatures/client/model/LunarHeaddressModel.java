package net.msrandom.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LunarHeaddressModel extends AnimatedGeoModel<LunarHeaddressItem> {
    public static ResourceLocation TEXTURE = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/models/armor/lunar_headdress.png");

    @Override
    public ResourceLocation getModelLocation(LunarHeaddressItem object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/lunar_headdress.geo.json");
    }

    @Override
    public ResourceLocation _getTextureLocation(LunarHeaddressItem object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(LunarHeaddressItem animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/lunar_headdress.animation.json");
    }
}
