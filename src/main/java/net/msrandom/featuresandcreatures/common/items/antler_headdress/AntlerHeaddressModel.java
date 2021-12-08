package net.msrandom.featuresandcreatures.common.items.antler_headdress;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AntlerHeaddressModel extends AnimatedGeoModel<AntlerHeaddressItem> {


    @Override
    public ResourceLocation getModelLocation(AntlerHeaddressItem object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/antler_headdress.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(AntlerHeaddressItem object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "textures/models/armor/antler_headdress.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(AntlerHeaddressItem animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/antler_headdress.animation.json");
    }
}
