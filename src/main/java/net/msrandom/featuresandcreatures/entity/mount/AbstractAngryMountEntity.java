package net.msrandom.featuresandcreatures.entity.mount;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.msrandom.featuresandcreatures.entity.mount.goal.MountAttackGoal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractAngryMountEntity extends AbstractMountEntity implements IAngerable {
    protected static final DataParameter<Boolean> ATTACKING = EntityDataManager.defineId(AbstractAngryMountEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> REMAINING_PERSISTENT_ANGER_TIME = EntityDataManager.defineId(AbstractAngryMountEntity.class, DataSerializers.INT);
    protected static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);

    private UUID persistentAngerTarget;

    protected AbstractAngryMountEntity(EntityType<? extends AbstractAngryMountEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MountAttackGoal<>(this));
        targetSelector.addGoal(5, new ResetAngerGoal<>(this, false));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ATTACKING, false);
        entityData.define(REMAINING_PERSISTENT_ANGER_TIME, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (!level.isClientSide) {
            readPersistentAngerSaveData((ServerWorld) level, compoundNBT);
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
    public boolean hurt(DamageSource damageSource, float amount) {
        if (!level.isClientSide) {
            @Nullable Entity entity = damageSource.getEntity();
            if (entity != null) {
                startPersistentAngerTimer();
                setPersistentAngerTarget(entity.getUUID());
            }
        }
        return super.hurt(damageSource, amount);
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
    protected <T extends IAnimatable> PlayState getPlayState(AnimationEvent<T> event) {
        AnimationController<?> animationController = event.getController();
        animationController.transitionLengthTicks = 0;
        int attackingAnimationTime = getAnimationTime();
        if (event.isMoving() && attackingAnimationTime <= 0) {
            animationController.setAnimation(new AnimationBuilder().addAnimation(getWalkAnimation(), true));
            return PlayState.CONTINUE;
        } else if (attackingAnimationTime > 0) {
            animationController.setAnimation(new AnimationBuilder().addAnimation(getAttackAnimation(), true));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    protected abstract @NotNull String getWalkAnimation();

    protected abstract @NotNull String getAttackAnimation();

    @Override
    public boolean canAttack(LivingEntity livingEntity) {
        return true;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return entityData.get(REMAINING_PERSISTENT_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {
        entityData.set(REMAINING_PERSISTENT_ANGER_TIME, i);
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
}
