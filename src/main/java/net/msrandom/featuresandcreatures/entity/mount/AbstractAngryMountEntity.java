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
import net.minecraft.world.Difficulty;
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
        return livingEntity.isAlive() && level.getDifficulty() != Difficulty.PEACEFUL;
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
}
