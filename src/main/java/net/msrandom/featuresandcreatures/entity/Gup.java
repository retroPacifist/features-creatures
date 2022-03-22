package net.msrandom.featuresandcreatures.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class Gup extends Monster implements IAnimatable, RangedAttackMob {

    private final AnimationFactory factory = new AnimationFactory(this);

    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(Gup.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TIME = SynchedEntityData.defineId(Gup.class, EntityDataSerializers.INT);

    public Gup(EntityType<? extends Gup> type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 56.0).add(Attributes.ATTACK_DAMAGE, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.15F).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 10F));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ATTACKING, false);
        entityData.define(ATTACK_TIME, 32);
    }
    public int getAttackTimer() {
        return entityData.get(ATTACK_TIME);
    }

    public void setAttackTimer(int time){
        entityData.set(ATTACK_TIME, time);
    }

    public boolean isAttacking() {
        return entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        entityData.set(ATTACKING, attacking);
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (this.isOnGround() && this.isAttacking()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.gup.spikes", true));
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

    @Override
    public void performRangedAttack(LivingEntity entity, float p_33318_) {
        if(!this.isAttacking()) {
            Snowball snowball = new Snowball(this.level, this);
            Snowball snowball2 = new Snowball(this.level, this);
            Snowball snowball3 = new Snowball(this.level, this);
            Snowball snowball4 = new Snowball(this.level, this);
            double d0 = entity.getEyeY() - (double) 1.1F;
            double d1 = entity.getX() - this.getX();
            double d2 = d0 - snowball.getY();
            double d3 = entity.getZ() - this.getZ();
            double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double) 0.2F;
            snowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
            snowball2.shoot(-d1, d2 + d4, -d3, 1.6F, 12.0F);
            snowball3.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
            snowball4.shoot(-d1, d2 + d4, d3, 1.6F, 12.0F);
            this.setAttacking(true);
            this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level.addFreshEntity(snowball);
            this.level.addFreshEntity(snowball2);
            this.level.addFreshEntity(snowball3);
            this.level.addFreshEntity(snowball4);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.isAttacking()){
            this.setAttackTimer(this.getAttackTimer() - 1);
        }
        if (this.getAttackTimer() <= 0){
            this.setAttackTimer(32);
            this.setAttacking(false);
        }
    }
}
