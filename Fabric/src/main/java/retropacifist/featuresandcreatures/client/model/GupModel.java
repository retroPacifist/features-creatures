package retropacifist.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.entity.Gup;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static retropacifist.featuresandcreatures.FeaturesAndCreatures.createResourceLocation;

public class GupModel extends AnimatedGeoModel<Gup> {
    public static final ResourceLocation TEXTURE = createResourceLocation("textures/entity/gup.png");


    @Override
    public ResourceLocation getModelLocation(Gup object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/gup.geo.json");
    }

    @Override
    public ResourceLocation _getTextureLocation(Gup object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Gup animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/gup.animation.json");
    }
}