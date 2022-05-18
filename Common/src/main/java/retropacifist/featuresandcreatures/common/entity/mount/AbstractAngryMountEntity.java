package retropacifist.featuresandcreatures.common.entity.mount;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retropacifist.featuresandcreatures.common.entity.mount.goal.MountAttackGoal;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractAngryMountEntity extends AbstractMountEntity implements NeutralMob {
    protected static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(AbstractAngryMountEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> REMAINING_PERSISTENT_ANGER_TIME = SynchedEntityData.defineId(AbstractAngryMountEntity.class, EntityDataSerializers.INT);
    protected static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private UUID persistentAngerTarget;

    protected AbstractAngryMountEntity(EntityType<? extends AbstractAngryMountEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new MountAttackGoal<>(this));
        targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ATTACKING, false);
        entityData.define(REMAINING_PERSISTENT_ANGER_TIME, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (!level.isClientSide) {
            readPersistentAngerSaveData((ServerLevel) level, compoundNBT);
        }
        setAttacking(compoundNBT.getBoolean("Attacking"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundNBT) {
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
        setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(random));
    }

    public boolean isAttacking() {
        return entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        entityData.set(ATTACKING, attacking);
    }
}
