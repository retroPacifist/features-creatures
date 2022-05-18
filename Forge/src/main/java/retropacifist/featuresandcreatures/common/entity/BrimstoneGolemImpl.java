package retropacifist.featuresandcreatures.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BrimstoneGolemImpl extends BrimstoneGolem implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public BrimstoneGolemImpl(EntityType<? extends AbstractGolem> type, Level world) {
        super(type, world);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (getAttackAnimationTick() >= 1) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.brimstonegolem.attackleft", true));
            return PlayState.CONTINUE;
        }
        if (getRangeAttackAnimationTick() >= 1) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.brimstonegolem.attackright", true));
            return PlayState.CONTINUE;
        }
        if (this.isOnGround() && !this.isAngry()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.brimstonegolem.idle", true));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

}
