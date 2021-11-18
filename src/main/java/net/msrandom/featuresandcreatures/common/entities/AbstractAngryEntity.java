package net.msrandom.featuresandcreatures.common.entities;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.UUID;

public class AbstractAngryEntity extends AnimalEntity implements IAngerable, IAnimatable {

    private static final DataParameter<Boolean> DATA_STANDING_ID = EntityDataManager.defineId(AbstractAngryEntity.class, DataSerializers.BOOLEAN);
    private static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);
    private int warningSoundTicks;
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;

    protected AbstractAngryEntity(EntityType<? extends AbstractAngryEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new AbstractAngryEntity.MeleeAttackGoal());
        this.goalSelector.addGoal(1, new AbstractAngryEntity.PanicGoal());
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new AbstractAngryEntity.HurtByTargetGoal());
        this.targetSelector.addGoal(2, new AbstractAngryEntity.AttackPlayerGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, false));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STANDING_ID, false);
    }

    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (!level.isClientSide)
            this.readPersistentAngerSaveData((ServerWorld) this.level, nbt);
    }

    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        this.addPersistentAngerSaveData(nbt);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
    }

    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    public void setRemainingPersistentAngerTime(int time) {
        this.remainingPersistentAngerTime = time;
    }

    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    public boolean isStanding() {
        return this.entityData.get(DATA_STANDING_ID);
    }

    public void setStanding(boolean p_189794_1_) {
        this.entityData.set(DATA_STANDING_ID, p_189794_1_);
    }

    public SoundEvent getWarningSound() {
        return SoundEvents.POLAR_BEAR_WARNING;
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(getWarningSound(), 1.0F, this.getVoicePitch());
            this.warningSoundTicks = 40;
        }
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return true;
    }

    @Override
    public void registerControllers(AnimationData data) {
    }

    @Override
    public AnimationFactory getFactory() {
        return null;
    }

    protected class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
        public AttackPlayerGoal() {
            super(AbstractAngryEntity.this, PlayerEntity.class, 20, true, true, null);
        }

        public boolean canUse() {
            if (!isBaby()) {
                if (super.canUse()) {
                    for (AbstractAngryEntity entity : level.getEntitiesOfClass(AbstractAngryEntity.class, getBoundingBox().inflate(8.0D, 4.0D, 8.0D))) {
                        if (entity.isBaby()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.5D;
        }
    }

    protected class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(AbstractAngryEntity.this);
        }

        public void start() {
            super.start();
            if (isBaby()) {
                this.alertOthers();
                this.stop();
            }

        }

        protected void alertOther(MobEntity mob, LivingEntity attacker) {
            if (mob instanceof AbstractAngryEntity && !mob.isBaby()) {
                super.alertOther(mob, attacker);
            }
        }
    }

    protected class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {

        public MeleeAttackGoal() {
            super(AbstractAngryEntity.this, 1.24D, true);
        }

        protected void checkAndPerformAttack(LivingEntity entity, double time) {
            double d0 = this.getAttackReachSqr(entity);
            if (time <= d0 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(entity);
                setStanding(false);
            } else if (time <= d0 * 2.0D) {
                if (this.isTimeToAttack()) {
                    setStanding(false);
                    this.resetAttackCooldown();
                }
                if (this.getTicksUntilNextAttack() <= 20) {
                    setStanding(true);
                    playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
                setStanding(false);
            }
        }

        public void stop() {
            setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity entity) {
            return 1.7F + entity.getBbWidth();
        }
    }

    class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
        public PanicGoal() {
            super(AbstractAngryEntity.this, 2.0D);
        }

        public boolean canUse() {
            return (isBaby() || isOnFire()) && super.canUse();
        }
    }
}