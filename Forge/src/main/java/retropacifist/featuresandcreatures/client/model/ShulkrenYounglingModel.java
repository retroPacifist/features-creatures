package retropacifist.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.entity.ShulkrenYounglingImpl;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static retropacifist.featuresandcreatures.FeaturesAndCreatures.createResourceLocation;

public class ShulkrenYounglingModel extends AnimatedGeoModel<ShulkrenYounglingImpl> {
    public static final ResourceLocation TEXTURE = createResourceLocation("textures/entity/shulkren_youngling.png");


    @Override
    public ResourceLocation getModelLocation(ShulkrenYounglingImpl object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/shulkren_youngling.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ShulkrenYounglingImpl object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ShulkrenYounglingImpl animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/shulkren_youngling.animation.json");
    }
}