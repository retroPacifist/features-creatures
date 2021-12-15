package net.msrandom.featuresandcreatures.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.UUID;

public class AbstractAngryEntity extends Animal implements NeutralMob, IAnimatable {

    private static final DataParameter<Boolean> SADDLED = SynchedEntityData.defineId(AbstractAngryEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_STANDING_ID = SynchedEntityData.defineId(AbstractAngryEntity.class, DataSerializers.BOOLEAN);
    private static final RangedInteger PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int warningSoundTicks;
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;

    protected AbstractAngryEntity(EntityType<? extends AbstractAngryEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new AttackGoal());
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.32D));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CallForHelpGoal());
        this.targetSelector.addGoal(2, new AbstractAngryEntity.AttackPlayerGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void setBaby(boolean p_82227_1_) {
        super.setBaby(p_82227_1_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STANDING_ID, false);
        this.entityData.define(SADDLED, false);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (!level.isClientSide) {
            this.readPersistentAngerSaveData(this.level, nbt);
            this.setSaddled(nbt.getBoolean("Saddled"));

        }
    }

    @Override
    public ActionResultType mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (player.isHolding(Items.SADDLE) && !this.isBaby()) {
            this.setSaddled(true);
            if (!player.isCreative()) {
                player.level.playSound(null, this.getX(), this.getY() + 0.3f, this.getZ(), SoundEvents.PIG_SADDLE, SoundSource.AMBIENT, 1, 1);
                player.getItemInHand(hand).shrink(1);
            }
        }
        if (player.isCrouching() && player.getItemInHand(hand).getItem() != Items.SADDLE && this.isSaddled()) {
            this.setSaddled(false);
            player.level.addFreshEntity(new ItemEntity(player.level, this.getX(), this.getY() + 0.3f, this.getZ(), Items.SADDLE.getDefaultInstance()));
            player.level.playSound(null, this.getX(), this.getY() + 0.3f, this.getZ(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.AMBIENT, 1, 1);
        }
        return ActionResultType.SUCCESS;
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        this.addPersistentAngerSaveData(nbt);
        nbt.putBoolean("Saddled", this.isSaddled());

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
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
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

    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, saddled);
    }

    protected class AttackPlayerGoal extends NearestAttackableTargetGoal<Player> {
        public AttackPlayerGoal() {
            super(AbstractAngryEntity.this, Player.class, 20, true, true, null);
        }

        public boolean canUse() {
            if (!isBaby()) {
                if (super.canUse()) {
                    for (AbstractAngryEntity entity : level.getEntitiesOfClass(AbstractAngryEntity.class, getBoundingBox().inflate(6.0D, 4.0D, 6.0D))) {
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

    protected class CallForHelpGoal extends HurtByTargetGoal {
        public CallForHelpGoal() {
            super(AbstractAngryEntity.this);
        }

        public void start() {
            super.start();
            if (isBaby()) {
                this.alertOthers();
                this.stop();
            }

        }

        protected void alertOther(Mob mob, LivingEntity attacker) {
            if (mob instanceof AbstractAngryEntity && !mob.isBaby()) {
                super.alertOther(mob, attacker);
            }
        }
    }

    protected class AttackGoal extends MeleeAttackGoal {

        public AttackGoal() {
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
            return 4F + entity.getBbWidth();
        }
    }
}
