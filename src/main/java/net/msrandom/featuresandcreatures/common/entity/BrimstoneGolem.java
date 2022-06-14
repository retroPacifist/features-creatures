package net.msrandom.featuresandcreatures.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

public class BrimstoneGolem extends AbstractGolem implements NeutralMob, RangedAttackMob, IAnimatable, FloatingEntity {

    private final AnimationFactory factory = new AnimationFactory(this);

    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(BrimstoneGolem.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Integer> ATTACK_TIME = SynchedEntityData.defineId(BrimstoneGolem.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> RANGE_ATTACK_TIME = SynchedEntityData.defineId(BrimstoneGolem.class, EntityDataSerializers.INT);

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;
    public MeleeAttackGoal meleeAttackGoal;
    public RangedAttackGoal rangedAttackGoal;


    public BrimstoneGolem(EntityType<? extends AbstractGolem> type, Level world) {
        super(type, world);
        this.maxUpStep = 1.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 64.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.ATTACK_DAMAGE, 22.0D);
    }

    public static boolean checkSpawnRules(EntityType<BrimstoneGolem> type, LevelAccessor world, MobSpawnType spawnType, BlockPos pos, Random random) {
        if (world.getBiome(pos).is(new ResourceLocation("byg", "brimstone_caverns"))){
            return checkMobSpawnRules(type, world, spawnType, pos, random);
        }
        if (world.getBlockState(pos.below()).is(Blocks.POLISHED_BLACKSTONE_BRICKS)) {
            return checkMobSpawnRules(type, world, spawnType, pos, random);
        }
        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        meleeAttackGoal = new MeleeAttackGoal(this, 1.25D, true);
        rangedAttackGoal = new RangedAttackGoal(this, 1.25D, 20, 10.0F);
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (entity) -> entity instanceof Enemy && !(entity instanceof AbstractPiglin)));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
        registerAttackGoals();
    }


    private void registerAttackGoals() {
        this.goalSelector.addGoal(1, meleeAttackGoal);
    }

    @Override
    protected float getBlockSpeedFactor() {
        BlockState blockstate = this.level.getBlockState(this.blockPosition());
        float f = blockstate.getBlock().getSpeedFactor();
        if (!blockstate.is(Blocks.WATER) && !blockstate.is(Blocks.BUBBLE_COLUMN)) {
            return (double)f == 1.0D ? this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getSpeedFactor() : f;
        } else {
            return 1.0F;
        }
    }

    @Override
    public float getBlockJumpFactor() {
        return 1.0F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getTarget() != null) {
            if (this.getTarget().distanceToSqr(this.getX(), this.getY(), this.getZ()) > 120 || (this.getTarget().position().y - this.position().y) > 4) {
                if (this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal().getClass() == MeleeAttackGoal.class)) {
                    this.goalSelector.removeGoal(meleeAttackGoal);
                    this.goalSelector.addGoal(1, rangedAttackGoal);
                }
            } else {
                if (this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal().getClass() == RangedAttackGoal.class)) {
                    this.goalSelector.removeGoal(rangedAttackGoal);
                    this.goalSelector.addGoal(1, meleeAttackGoal);
                }
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag p_28867_) {
        super.addAdditionalSaveData(p_28867_);
        p_28867_.putBoolean("PlayerCreated", this.isPlayerCreated());
        this.addPersistentAngerSaveData(p_28867_);
    }

    public void readAdditionalSaveData(CompoundTag p_28857_) {
        super.readAdditionalSaveData(p_28857_);
        this.setPlayerCreated(p_28857_.getBoolean("PlayerCreated"));
        this.readPersistentAngerSaveData(this.level, p_28857_);
    }

    public void aiStep() {
        super.aiStep();
        if (this.getAttackAnimationTick() > 0) {
            this.setAttackAnimationTick(this.getAttackAnimationTick() - 1);
        }
        if (this.getRangeAttackAnimationTick() > 0) {
            this.setRangeAttackAnimationTick(this.getRangeAttackAnimationTick() - 1);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    private float getAttackDamage() {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.setAttackAnimationTick(31);
        this.level.broadcastEntityEvent(this, (byte) 4);
        float f = this.getAttackDamage();
        float f1 = (int) f > 0 ? f / 2.0F + (float) this.random.nextInt((int) f) : f;
        boolean flag = entity.hurt(DamageSource.mobAttack(this), f1);
        if (flag) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, (double) 0.4F, 0.0D));
            this.doEnchantDamageEffects(this, entity);
            entity.setSecondsOnFire(10);
        }
        this.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 1.0F, 1.0F);
        if (getAttackAnimationTick() <= 1) {
            return flag;
        }
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(ATTACK_TIME, 0);
        this.entityData.define(RANGE_ATTACK_TIME, 0);
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    public void setRemainingPersistentAngerTime(int p_28859_) {
        this.remainingPersistentAngerTime = p_28859_;
    }

    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    public void setPersistentAngerTarget(@javax.annotation.Nullable UUID p_28855_) {
        this.persistentAngerTarget = p_28855_;
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public boolean isPlayerCreated() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setPlayerCreated(boolean p_28888_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_28888_) {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 | 1));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte) (b0 & -2));
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    public int getAttackAnimationTick() {
        return entityData.get(ATTACK_TIME);
    }

    public void setAttackAnimationTick(int tick) {
        entityData.set(ATTACK_TIME, tick);
    }

    public int getRangeAttackAnimationTick() {
        return entityData.get(RANGE_ATTACK_TIME);
    }

    public void setRangeAttackAnimationTick(int tick) {
        entityData.set(RANGE_ATTACK_TIME, tick);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (getAttackAnimationTick() >= 1) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.brimstonegolem.attackleft", true));
            return PlayState.CONTINUE;
        }
        if (getRangeAttackAnimationTick() >= 1) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.brimstonegolem.attackright", true));
            return PlayState.CONTINUE;
        }
        if (this.isOnGround() && !this.isAngry()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.brimstonegolem.idle", true));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float p_33318_) {
        Vec3 vec3 = this.getViewVector(1.0F);
        double d2 = entity.getX() - (this.getX() + vec3.x * 4.0D);
        double d3 = entity.getY(0.5D) - (0.5D + this.getY(0.5D));
        double d4 = entity.getZ() - (this.getZ() + vec3.z * 4.0D);
        LargeFireball largefireball = new LargeFireball(level, this, d2, d3, d4, 0);
        largefireball.setPos(this.getX() + vec3.x * 4.0D, this.getY(0.5D) + 0.5D, largefireball.getZ() + vec3.z * 4.0D);
        level.addFreshEntity(largefireball);
        setRangeAttackAnimationTick(34);
    }
}
