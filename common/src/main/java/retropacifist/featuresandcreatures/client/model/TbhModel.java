package retropacifist.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.entity.Tbh;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class TbhModel extends AnimatedGeoModel<Tbh> {
    public static final ResourceLocation TEXTURE = FeaturesAndCreatures.createResourceLocation("textures/entity/tbh.png");


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

    @Override
    public void setLivingAnimations(Tbh entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
    }
}