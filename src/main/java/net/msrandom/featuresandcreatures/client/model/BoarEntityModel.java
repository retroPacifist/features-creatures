package net.msrandom.featuresandcreatures.client.model;

import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.entity.mount.BoarEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createResourceLocation;

public final class BoarEntityModel extends AbstractMountModel<BoarEntity> {
    private static final ResourceLocation MODEL = createResourceLocation("geo/boar.geo.json");
    private static final ResourceLocation SADDLED_TEXTURE = createResourceLocation("textures/entity/boar_saddle.png");
    private static final ResourceLocation ANGRY_TEXTURE = createResourceLocation("textures/entity/boar_angry.png");
    private static final ResourceLocation TEXTURE = createResourceLocation("textures/entity/boar.png");
    private static final ResourceLocation ANIMATION = createResourceLocation("animations/boar.animation.json");

    @Override
    public void setLivingAnimations(BoarEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
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

    @Override
    public @NotNull ResourceLocation getSaddledTexture(BoarEntity boarEntity) {
        return SADDLED_TEXTURE;
    }

    @Override
    public @NotNull ResourceLocation getRegularTexture(BoarEntity boarEntity) {
        return boarEntity.isAngry() ? ANGRY_TEXTURE : TEXTURE;
    }

    @Override
    public ResourceLocation getModelLocation(BoarEntity object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BoarEntity animatable) {
        return ANIMATION;
    }
}