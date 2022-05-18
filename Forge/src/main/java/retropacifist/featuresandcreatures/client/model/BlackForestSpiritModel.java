package retropacifist.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.entity.BlackForestSpiritImpl;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

import static retropacifist.featuresandcreatures.FeaturesAndCreatures.createResourceLocation;

public class BlackForestSpiritModel extends AnimatedGeoModel<BlackForestSpiritImpl> {
    public static final ResourceLocation TEXTURE = createResourceLocation("textures/entity/spirit/black_forest_spirit.png");
    public static final ResourceLocation GLOW_TEXTURE = createResourceLocation("textures/entity/spirit/black_forest_spirit_glow.png");


    @Override
    public ResourceLocation getModelLocation(BlackForestSpiritImpl object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/black_forest_spirit.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(BlackForestSpiritImpl object) {
        return object.hasLapis() ? GLOW_TEXTURE : TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BlackForestSpiritImpl animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/black_forest_spirit.animation.json");
    }
    @Override
    public void setLivingAnimations(BlackForestSpiritImpl entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
    }
}