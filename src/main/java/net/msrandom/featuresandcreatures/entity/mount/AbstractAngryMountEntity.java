package net.msrandom.featuresandcreatures.entity.mount;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractAngryMountEntity extends AbstractMountEntity implements IAngerable {
    protected static final DataParameter<Boolean> ATTACKING = EntityDataManager.defineId(AbstractAngryMountEntity.class, DataSerializers.BOOLEAN);
    protected static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);

    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;

    protected AbstractAngryMountEntity(EntityType<? extends AbstractAngryMountEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MountAttackGoal<>(this));
        targetSelector.addGoal(2, new HuntDownPlayerGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ATTACKING, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (!level.isClientSide) {
            readPersistentAngerSaveData((ServerWorld) level, compoundNBT) ;
        }
        setAttacking(compoundNBT.getBoolean("Attacking"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        addPersistentAngerSaveData(compoundNBT);
        compoundNBT.putBoolean("Attacking", isAttacking());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (isAttacking()) {
                setAnimationTime(15);
            }

            int animationTime = getAnimationTime();
            if (animationTime > 0) {
                setAnimationTime(animationTime - 1);
            }
        }
    }

    @Override
    public boolean canAttack(LivingEntity livingEntity) {
        return true;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {
        remainingPersistentAngerTime = i;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        persistentAngerTarget = uuid;
    }

    @Override
    public void startPersistentAngerTimer() {
        setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(random));
    }

    public boolean isAttacking() {
        return entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        entityData.set(ATTACKING, attacking);
    }

    protected static final class HuntDownPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {

        public HuntDownPlayerGoal(MobEntity mobEntity) {
            super(mobEntity, PlayerEntity.class, 20, true, true, null);
        }

        @Override
        public boolean canUse() {
            return super.canUse();
        }
    }

    protected static final class MountAttackGoal<T extends AbstractAngryMountEntity> extends MeleeAttackGoal {
        private final T entity;

        public MountAttackGoal(T entity) {
            super(entity, 1.24D, true);
            this.entity = entity;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity livingEntity, double attackTime) {
            double attackReachRqr = getAttackReachSqr(livingEntity);
            if (isTimeToAttack()) {
                if (attackTime <= attackReachRqr) {
                    resetAttackCooldown();
                    entity.doHurtTarget(livingEntity);
                    entity.setAttacking(true);
                } else if (attackTime < attackReachRqr * 2.0D) {
                    resetAttackCooldown();
                    entity.setAttacking(true);
                }
            } else {
                entity.setAttacking(!(attackTime < attackReachRqr * 2.0D));
            }
        }

        @Override
        public void stop() {
            entity.setAttacking(false);
            super.stop();
        }

        @Override
        protected double getAttackReachSqr(LivingEntity livingEntity) {
            return super.getAttackReachSqr(livingEntity);
        }
    }
}
