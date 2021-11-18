package net.msrandom.featuresandcreatures.common.entities.boar;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

import static net.msrandom.featuresandcreatures.common.entities.boar.BoarRenderer.*;

public class BoarModel extends AnimatedGeoModel<Boar> {

    @Override
    public ResourceLocation getModelLocation(Boar object) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "geo/boar.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Boar object) {
        if(object.isSaddled()){
            return SADDLED;
        } else if(object.isAngry()){
            return MADTEXTURE;
        } else return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Boar animatable) {
        return new ResourceLocation(FeaturesAndCreatures.MOD_ID, "animations/boar.animation.json");
    }

    @Override
    public void setLivingAnimations(Boar entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
    }
}