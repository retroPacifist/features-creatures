package retropacifist.featuresandcreatures.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TbhImpl extends Tbh implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    public TbhImpl(EntityType<? extends Tbh> entityType, Level level) {
        super(entityType, level);
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
        if (this.isOnGround() && !event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.tbh.idle", true));
            return PlayState.CONTINUE;
        }
        if (this.isOnGround() && event.isMoving() && !this.isRunning()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.tbh.walk", true));
            return PlayState.CONTINUE;
        }
        if (this.isRunning()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.tbh.run", true));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

}
