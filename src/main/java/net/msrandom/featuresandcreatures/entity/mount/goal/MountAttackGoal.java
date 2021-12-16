package net.msrandom.featuresandcreatures.entity.mount.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.msrandom.featuresandcreatures.entity.mount.AbstractAngryMountEntity;
import org.jetbrains.annotations.NotNull;

public final class MountAttackGoal<T extends AbstractAngryMountEntity> extends MeleeAttackGoal {
    private final T entity;

    public MountAttackGoal(T entity) {
        super(entity, 1.24D, true);
        this.entity = entity;
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity livingEntity, double entityDistance) {
        double attackRange = getAttackReachSqr(livingEntity);
        if (entityDistance <= attackRange && isTimeToAttack()) {
            resetAttackCooldown();
            entity.setAttacking(true);
            entity.doHurtTarget(livingEntity);
        } else {
            entity.setAttacking(false);
        }
    }

    @Override
    public void stop() {
        entity.setAttacking(false);
        super.stop();
    }

    @Override
    protected double getAttackReachSqr(LivingEntity entity) {
        return 4F + entity.getBbWidth();
    }
}
