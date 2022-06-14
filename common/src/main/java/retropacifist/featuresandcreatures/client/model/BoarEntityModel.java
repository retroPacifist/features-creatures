package retropacifist.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.entity.mount.Boar;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public final class BoarEntityModel extends AbstractMountModel<Boar> {
    private static final ResourceLocation MODEL = FeaturesAndCreatures.createResourceLocation("geo/boar.geo.json");
    private static final ResourceLocation SADDLED_TEXTURE = FeaturesAndCreatures.createResourceLocation("textures/entity/boar_saddle.png");
    private static final ResourceLocation SADDLED_ANGRY_TEXTURE = FeaturesAndCreatures.createResourceLocation("textures/entity/boar_saddle_angry.png");
    private static final ResourceLocation ANGRY_TEXTURE = FeaturesAndCreatures.createResourceLocation("textures/entity/boar_angry.png");
    private static final ResourceLocation TEXTURE = FeaturesAndCreatures.createResourceLocation("textures/entity/boar.png");
    private static final ResourceLocation ANIMATION = FeaturesAndCreatures.createResourceLocation("animations/boar.animation.json");

    @Override
    public void setLivingAnimations(Boar entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
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
    public @NotNull ResourceLocation getSaddledTexture(Boar boarEntity) {
        return boarEntity.isAngry() ? SADDLED_ANGRY_TEXTURE : SADDLED_TEXTURE;
    }

    @Override
    public @NotNull ResourceLocation getRegularTexture(Boar boarEntity) {
        return boarEntity.isAngry() ? ANGRY_TEXTURE : TEXTURE;
    }

    @Override
    public ResourceLocation getModelLocation(Boar object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Boar animatable) {
        return ANIMATION;
    }
}