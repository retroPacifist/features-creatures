package net.msrandom.featuresandcreatures.common.entities.boar;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.UUID;

public class Boar extends AnimalEntity implements IAngerable, IAnimatable {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CARROT, Items.POTATO, Items.BEETROOT);
    private static final DataParameter<Boolean> DATA_STANDING_ID = EntityDataManager.defineId(Boar.class, DataSerializers.BOOLEAN);
    private static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);
    private final AnimationFactory factory = new AnimationFactory(this);
    private int warningSoundTicks;
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;

    public Boar(EntityType<? extends Boar> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 11.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new Boar.MeleeAttackGoal());
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, false, FOOD_ITEMS));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new Boar.HurtByTargetGoal());
        this.targetSelector.addGoal(2, new Boar.AttackPlayerGoal());
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

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIG_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PIG_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PIG_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.PIG_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEvents.PIG_HURT, 1.0F, this.getVoicePitch());
            this.warningSoundTicks = 40;
        }
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return true;
    }

    @Override
    public Boar getBreedOffspring(ServerWorld world, AgeableEntity mate) {
        Boar boar = FnCEntities.BOAR.get().create(world);
        boar.setAge(-24000);
        return boar;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (this.isOnGround() && event.isMoving() && controller.getName() != "animation.boar.attack") {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.boar.walk", true));
            return PlayState.CONTINUE;
        } else if (this.isStanding()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.boar.attack", true));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public boolean isStanding() {
        return this.entityData.get(DATA_STANDING_ID);
    }

    public void setStanding(boolean p_189794_1_) {
        this.entityData.set(DATA_STANDING_ID, p_189794_1_);
    }

    class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
        public AttackPlayerGoal() {
            super(Boar.this, PlayerEntity.class, 20, true, true, null);
        }

        public boolean canUse() {
            if (!Boar.this.isBaby()) {
                if (super.canUse()) {
                    for (Boar boar : Boar.this.level.getEntitiesOfClass(Boar.class, Boar.this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D))) {
                        if (boar.isBaby()) {
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

    class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(Boar.this);
        }

        public void start() {
            super.start();
            if (Boar.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }

        }

        protected void alertOther(MobEntity mob, LivingEntity attacker) {
            if (mob instanceof Boar && !mob.isBaby()) {
                super.alertOther(mob, attacker);
            }
        }
    }


    class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {

        public MeleeAttackGoal() {
            super(Boar.this, 1.24D, true);
        }


        protected void checkAndPerformAttack(LivingEntity entity, double time) {
            double d0 = this.getAttackReachSqr(entity);
            if (time <= d0 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(entity);
                Boar.this.setStanding(false);
            } else if (time <= d0 * 2.0D) {
                if (this.isTimeToAttack()) {
                    Boar.this.setStanding(false);
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 20) {
                    Boar.this.setStanding(true);
                    Boar.this.playWarningSound();
                    Boar.this.setDeltaMovement(0, 0, 0);
                }
            } else {
                this.resetAttackCooldown();
                Boar.this.setStanding(false);
            }

        }

        public void stop() {
            Boar.this.setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity entity) {
            return 3.0F + entity.getBbWidth();
        }
    }

}
