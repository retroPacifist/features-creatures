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

public class JockeyImpl extends Jockey implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public JockeyImpl(EntityType<? extends Jockey> p_i48575_1_, Level p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (this.isOnGround() && event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jockey.walk", true));
            return PlayState.CONTINUE;
        } else if (this.isAttacking()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jockey.potion", true));
            return PlayState.CONTINUE;
        } else if (this.isHolding(Items.POTION)) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jockey.drink", true));
            return PlayState.CONTINUE;
        }
        if (isRiding(this)) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jockey.sit", true));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }


}
