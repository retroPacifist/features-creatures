package retropacifist.featuresandcreatures.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ShulkrenYounglingImpl extends ShulkrenYoungling implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public ShulkrenYounglingImpl(EntityType<? extends ShulkrenYoungling> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (!this.isHolding(Items.ENDER_EYE) && this.isOnGround() && !event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.idle", true));
            return PlayState.CONTINUE;
        }
        if (!this.isHolding(Items.ENDER_EYE) && this.isOnGround() && event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.walk", true));
            return PlayState.CONTINUE;
        }
        if (this.isHolding(Items.ENDER_EYE)){
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.hold", true));
            return PlayState.CONTINUE;
        }
        else{
            return PlayState.STOP;
        }
    }

}
