package retropacifist.featuresandcreatures.common.entity.mount.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import retropacifist.featuresandcreatures.common.entity.mount.AbstractAngryMountEntity;

public final class MountAttackGoal<T extends AbstractAngryMountEntity> extends MeleeAttackGoal {
    private final T entity;

    public MountAttackGoal(T entity) {
        super(entity, 1.24D, true);
        this.entity = entity;
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity livingEntity, double entityDistance) {
        double attackRange = getAttackReachSqr(livingEntity);
        if (livingEntity instanceof Player player && player.getAbilities().instabuild) return;
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
