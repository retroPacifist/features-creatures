package net.msrandom.featuresandcreatures.common.entities.jockey;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.entities.jackalope.JackalopeRenderer;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class JockeyModel extends AnimatedGeoModel<Jockey> {

    @Override
    public ResourceLocation getModelLocation(Jockey object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/jockey.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Jockey object) {
        return JackalopeRenderer.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Jockey animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/jockey.animation.json");
    }

    @Override
    public void setLivingAnimations(Jockey entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
    }
}