package net.msrandom.featuresandcreatures.client.model;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.client.renderer.entity.JackalopeRenderer;
import net.msrandom.featuresandcreatures.entity.Jackalope;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class JackalopeModel extends AnimatedGeoModel<Jackalope> {

    @Override
    public ResourceLocation getModelLocation(Jackalope object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/jackalope.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Jackalope object) {
        return object.isSaddled() ? JackalopeRenderer.SADDLED : JackalopeRenderer.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Jackalope animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/jackalope.animation.json");
    }

    @Override
    public void setLivingAnimations(Jackalope entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
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