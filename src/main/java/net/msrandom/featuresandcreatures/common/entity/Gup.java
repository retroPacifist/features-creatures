package net.msrandom.featuresandcreatures.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class Gup extends PathfinderMob implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(Gup.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TIME = SynchedEntityData.defineId(Gup.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(Gup.class, EntityDataSerializers.INT);


    public Gup(EntityType<? extends Gup> type, Level world) {
        super(type, world);
        this.moveControl = new Gup.GupMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 56.0).add(Attributes.ATTACK_DAMAGE, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.FOLLOW_RANGE, 48.0D);
    }


    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource source) {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new Gup.GupFloatGoal(this));
        this.goalSelector.addGoal(4, new Gup.GupRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new Gup.GupKeepOnJumpingGoal(this));
        this.goalSelector.addGoal(3, new GupLeapGoal(this, 1F));
//        this.goalSelector.addGoal(2, new GupLookAtTargetGoal(this));
        this.goalSelector.addGoal(1, new GupAnimatedAttackGoal(this, 1, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ATTACKING, false);
        entityData.define(ATTACK_TIME, 32);
        this.entityData.define(SIZE, 1);

    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33601_, DifficultyInstance p_33602_, MobSpawnType p_33603_, @Nullable SpawnGroupData p_33604_, @Nullable CompoundTag p_33605_) {
        int i = this.random.nextInt(4);
        if (i == 0) {
            i++;
        }
        this.setSize(i, true);
        return super.finalizeSpawn(p_33601_, p_33602_, p_33603_, p_33604_, p_33605_);
    }

    public EntityType<? extends Gup> getType() {
        return FnCEntities.GUP.get();
    }


    public void remove(Entity.RemovalReason reason) {
        int i = this.getSize();
        if (!this.level.isClientSide && i > 1 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float) i / 4.0F;
            int j = i - 1;
            int k = 2;

            for (int l = 0; l < k; ++l) {
                float f1 = ((float) (l % 2) - 0.5F) * f;
                float f2 = ((float) (0) - 0.5F) * f;
                Gup gup = this.getType().create(this.level);
                if (this.isPersistenceRequired()) {
                    gup.setPersistenceRequired();
                }

                gup.setCustomName(component);
                gup.setNoAi(flag);
                gup.setInvulnerable(this.isInvulnerable());
                gup.setSize(j, true);
                gup.moveTo(this.getX() + (double) f1, this.getY() + 0.5D, this.getZ() + (double) f2, this.random.nextFloat() * 360.0F, 0.0F);
                this.level.addFreshEntity(gup);
            }
        }

        super.remove(reason);
    }

    public int getAttackTimer() {
        return entityData.get(ATTACK_TIME);
    }

    public void setAttackTimer(int time) {
        entityData.set(ATTACK_TIME, time);
    }

    public boolean isAttacking() {
        return entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        entityData.set(ATTACKING, attacking);
    }

    public int getSize() {
        return this.entityData.get(SIZE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Size", this.getSize() - 1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSize(tag.getInt("Size") + 1, false);
    }

    @Override
    protected Component getTypeName() {
        return switch (this.getSize()) {
            case 1 -> new TranslatableComponent("entity.featuresandcreatures.gip");
            case 2 -> new TranslatableComponent("entity.featuresandcreatures.geep");
            default -> new TranslatableComponent("entity.featuresandcreatures.gup");
        };
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        // Target the entity that damaged the Gup
        Entity damagingEntity = damageSource.getEntity();
        if (damagingEntity instanceof LivingEntity) {
            this.setTarget((LivingEntity) damagingEntity);
        }

        return super.hurt(damageSource, damage);
    }

    protected void setSize(int size, boolean adjustHealth) {
        this.entityData.set(SIZE, size);
        this.reapplyPosition();
        this.refreshDimensions();
        switch (size) {
            case 1 -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(14F);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2F);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2F);
            }
            case 2 -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(28F);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.175F);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.5F);
            }
            case 3 -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(56F);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.15F);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5F);
            }
        }
        if (adjustHealth) {
            this.setHealth(this.getMaxHealth());
        }

        this.xpReward = size;
    }

    public EntityDimensions getDimensions(Pose pose) {
        if (pose == Pose.DYING) {
            return super.getDimensions(pose);
        }

        float gupModelBlockHeight = 1.75f;
        float scalingFactor = 1;
        // Scale the gup's height to 1 block on the server thread only to avoid the hitbox having a different size when rendering
        if (!this.level.isClientSide) {
            scalingFactor /= gupModelBlockHeight;
        }
        
        switch (this.getSize()) {
            case 2 -> {
                scalingFactor *= 2.5;
            }
            case 3 -> {
                scalingFactor *= 5.0;
            }
        }

        return super.getDimensions(pose).scale(scalingFactor);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    public void doDamage() {
        AABB axisalignedbb = (new AABB(this.blockPosition())).inflate(1.5F).expandTowards(0.0D, 2.0D, 0.0D);
        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity le : list) {
            if (le instanceof Gup) return;
            le.hurt(DamageSource.GENERIC, (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (this.isInWater()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.gup.walk", true));
            return PlayState.CONTINUE;
        }
        if (this.isAttacking()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.gup.spikes", true));
            return PlayState.CONTINUE;
        }
        if (!this.isOnGround()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.gup.jump", true));
            return PlayState.CONTINUE;
        }
        if (this.fallDistance > 0) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.gup.jump", true));
            return PlayState.CONTINUE;
        }
        if (this.isOnGround() && event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.gup.walk", true));
            return PlayState.CONTINUE;
        } else if (this.isOnGround() && !event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.gup.idle", true));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    protected boolean doPlayJumpSound() {
        return this.getSize() > 0;
    }

    public static boolean checkSpawnRules(EntityType<Gup> type, LevelAccessor world, MobSpawnType spawnType, BlockPos pos, Random random) {
        if (world.getDifficulty() != Difficulty.PEACEFUL) {
            Holder<Biome> biome = world.getBiome(pos);
            if (biome.is(Biomes.SWAMP)) {
                return true;
            }

            if (!(world instanceof WorldGenLevel)) {
                return false;
            }

            if (biome.value().biomeCategory != Biome.BiomeCategory.THEEND && biome.value().biomeCategory != Biome.BiomeCategory.NETHER && !biome.is(Biomes.SWAMP) && world.getMoonBrightness() == 0.0F) {
                return checkMobSpawnRules(type, world, spawnType, pos, random);
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        System.out.println(isAttacking());
        if (this.isAttacking()) {
            this.setAttackTimer(this.getAttackTimer() + 1);
        } else{
            this.setAttackTimer(0);
        }
    }

    @Override
    protected float getJumpPower() {
        return 0.6F;
    }

    public static void spawnFromStruckSlime(Slime slime) {
        int size = slime.getSize();
        if (size == 1) return; // tiny slimes don't spawn anything
        Gup gup = new Gup(FnCEntities.GUP.get(), slime.level);
        gup.setPos(slime.getPosition(0));
        gup.setYRot(slime.getYRot());
        gup.setSize(switch (size) {
            default -> 1;   // gip
            case 4 -> 2;    // geep
            case 8 -> 3;    // gup (won't occur naturally)
            }, true);
        slime.discard();
        slime.level.addFreshEntity(gup);
    }

    static class GupFloatGoal extends Goal {
        private final Gup gup;

        public GupFloatGoal(Gup p_33655_) {
            this.gup = p_33655_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            p_33655_.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return (this.gup.isInWater() || this.gup.isInLava()) && this.gup.getMoveControl() instanceof Gup.GupMoveControl;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.gup.getRandom().nextFloat() < 0.8F) {
                this.gup.getJumpControl().jump();
            }

            ((Gup.GupMoveControl) this.gup.getMoveControl()).setWantedMovement(1.2D);
        }
    }

    static class GupKeepOnJumpingGoal extends Goal {
        private final Gup gup;

        public GupKeepOnJumpingGoal(Gup p_33660_) {
            this.gup = p_33660_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !this.gup.isPassenger();
        }

        public void tick() {
            ((Gup.GupMoveControl) this.gup.getMoveControl()).setWantedMovement(1.0D);
        }
    }

    static class GupMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final Gup gup;
        private boolean isAggressive;

        public GupMoveControl(Gup gup) {
            super(gup);
            this.gup = gup;
            this.yRot = 180.0F * gup.getYRot() / (float) Math.PI;
        }

        public void setDirection(float p_33673_, boolean p_33674_) {
            this.yRot = p_33673_;
            this.isAggressive = p_33674_;
        }

        public void setWantedMovement(double p_33671_) {
            this.speedModifier = p_33671_;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.isOnGround()) {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.gup.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.gup.getJumpControl().jump();
                        if (this.gup.doPlayJumpSound()) {
                            this.gup.playSound(SoundEvents.SLIME_JUMP, this.gup.getSoundVolume(), 1);
                        }
                    } else {
                        this.gup.xxa = 0.0F;
                        this.gup.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }

            }
        }
    }

    static class GupRandomDirectionGoal extends Goal {
        private final Gup gup;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public GupRandomDirectionGoal(Gup p_33679_) {
            this.gup = p_33679_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return this.gup.getTarget() == null && (this.gup.onGround || this.gup.isInWater() || this.gup.isInLava() || this.gup.hasEffect(MobEffects.LEVITATION)) && this.gup.getMoveControl() instanceof Gup.GupMoveControl;
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.gup.getRandom().nextInt(60));
                this.chosenDegrees = (float) this.gup.getRandom().nextInt(360);
            }

            ((Gup.GupMoveControl) this.gup.getMoveControl()).setDirection(this.chosenDegrees, false);
        }
    }

    static class GupLookAtTargetGoal extends Goal {
        private final Gup gup;

        public GupLookAtTargetGoal(Gup p_33648_) {
            this.gup = p_33648_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.gup.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return this.gup.canAttack(livingentity) && this.gup.getMoveControl() instanceof GupMoveControl;
            }
        }

        public void start() {
            super.start();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = this.gup.getTarget();
            if (livingentity == null) {
                return false;
            } else return this.gup.canAttack(livingentity);
        }

        public void tick() {
            LivingEntity livingentity = this.gup.getTarget();
            if (livingentity != null) {
                this.gup.lookAt(livingentity, 10.0F, 10.0F);
                gup.setAttacking(true);
                if (gup.getAttackTimer() <= 1) {
                    gup.doDamage();
                }
            }
            ((Gup.GupMoveControl) this.gup.getMoveControl()).setDirection(this.gup.getYRot(), true);
        }
    }

    public static class GupLeapGoal extends Goal {
        private final Gup gup;
        private LivingEntity target;
        private final float yd;

        public GupLeapGoal(Gup gup, float p_25493_) {
            this.gup = gup;
            this.yd = p_25493_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.gup.isVehicle()) {
                return false;
            } else {
                this.target = this.gup.getTarget();
                if (this.target == null) {
                    return false;
                } else {
                    double d0 = this.gup.distanceToSqr(this.target);
                    if (!(d0 < 4.0D) && !(d0 > 16.0D)) {
                        if (!this.gup.isOnGround() && this.gup.getAttackTimer() < 20) {
                            return false;
                        } else {
                            return this.gup.getRandom().nextInt(reducedTickDelay(5)) == 0;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        public boolean canContinueToUse() {
            return !this.gup.isOnGround();
        }

        public void start() {
            Vec3 vec3 = this.gup.getDeltaMovement();
            Vec3 vec31 = new Vec3(this.target.getX() - this.gup.getX(), 0.0D, this.target.getZ() - this.gup.getZ());
            if (vec31.lengthSqr() > 1.0E-7D) {
                vec31 = vec31.normalize().scale(0.4D).add(vec3.scale(0.2D));
            }

            this.gup.setDeltaMovement(vec31.x, (double)this.yd, vec31.z);
        }
    }

    static class GupAnimatedAttackGoal extends MeleeAttackGoal {
        public Gup gup;

        public GupAnimatedAttackGoal(PathfinderMob mob, double p_25553_, boolean p_25554_) {
            super(mob, p_25553_, p_25554_);
            gup = (Gup) mob;
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity livingEntity, double entityDistance) {
            double attackRange = getAttackReachSqr(livingEntity);
            if (livingEntity instanceof Player player && player.getAbilities().instabuild) return;
            if (entityDistance <= attackRange && isTimeToAttack()) {
                resetAttackCooldown();
                gup.setAttacking(true);
                if (this.gup.getAttackTimer() <= 30) {
                    gup.doHurtTarget(livingEntity);
                    gup.setAttacking(false);
                }
            }
        }

        @Override
        public void stop() {
            gup.setAttacking(false);
            super.stop();
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity livingentity = this.gup.getTarget();
            if (livingentity != null) {
                this.gup.lookAt(livingentity, 10.0F, 10.0F);
            }

            ((Gup.GupMoveControl)this.gup.getMoveControl()).setDirection(this.gup.getYRot(), true);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity entity) {
            return 4F + entity.getBbWidth();
        }
    }
}
