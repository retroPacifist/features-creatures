package retropacifist.featuresandcreatures.common.entity.goals;


import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class AnimatedAttackGoal<T extends PathfinderMob> extends Goal {
    public static final AttackMethod GUB_ATTACK = new AttackMethod() {};

    private final T t;
    private final int ticksInToAttack;
    private final PathFinder pathFinder;
    private final AttackMethod method;

    private float ticks;

    public AnimatedAttackGoal(T t, float seconds, PathFinder pathFinder, AttackMethod method) {
        this.t = t;
        this.ticksInToAttack = Math.round(seconds * 20.0F);
        this.pathFinder = pathFinder.setFlags(this);
        this.method = method;
    }

    @Override
    public boolean canUse() {
        return hasTarget();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        ticks = 0;

    }

    @Override
    public void tick() {
        ticks++;
        super.tick(); // If mixins are used.
        if (!hasTarget()) {
            return;
        }

        if (ticks % ticksInToAttack == 0) {
            List<Entity> list = t.level.getEntities(t, t.getBoundingBox().inflate(1.0D), entity -> method.canPerform(t, entity));
            if (!list.isEmpty()) {
                list.forEach(entity -> method.perform(t, entity));
            }
        }
    }

    private boolean hasTarget() {
        @Nullable LivingEntity target = t.getTarget();
        return target != null && target.isAlive() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target);
    }

    //wtf does this do
    // Override for other mobs. Default will be coded for the gup and it's variants.
    public static class PathFinder {
        private final float speed;
        private final boolean mustSeeTarget;

        public PathFinder(float speed, boolean mustSeeTarget) {
            this.speed = speed;
            this.mustSeeTarget = mustSeeTarget;
        }

        public PathFinder setFlags(Goal goal) {
            goal.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            return this;
        }
    }

    public interface AttackMethod {

        default boolean canPerform(Mob mob, Entity entity) {
            return mob.distanceToSqr(entity) < range(mob) && mob.getTarget() == entity;
        }

        default void perform(Mob mob, Entity entity) {
            mob.doHurtTarget(entity);
        }

        default float range(Mob mob) {
            return 1.0F;
        }
    }
}
