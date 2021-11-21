package net.msrandom.featuresandcreatures.common.entities.sabertooth;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class SabertoothModel extends AnimatedGeoModel<Sabertooth> {

    @Override
    public ResourceLocation getModelLocation(Sabertooth object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/sabertooth.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Sabertooth object) {
        return object.isSaddled() ? SabertoothRenderer.SADDLED : SabertoothRenderer.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Sabertooth animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/sabertooth.animation.json");
    }

    @Override
    public void setLivingAnimations(Sabertooth entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        if (entity.isBaby()) {
            head.setScaleX(2);
            head.setScaleY(2);
            head.setScaleZ(2);
        }
    }
}