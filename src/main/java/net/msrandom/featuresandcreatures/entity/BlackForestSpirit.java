package net.msrandom.featuresandcreatures.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class BlackForestSpirit extends Monster implements NeutralMob, RangedAttackMob, IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    private static final EntityDataAccessor<Boolean> HAS_LAPIS = SynchedEntityData.defineId(BlackForestSpirit.class, EntityDataSerializers.BOOLEAN);

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;


    public BlackForestSpirit(EntityType<? extends BlackForestSpirit> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FollowItemGoal(this, 1, 3,7));
        this.targetSelector.addGoal(1, (new SpiritTargetGoal(this)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.23F).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public boolean canPickUpLoot() {
        return true;
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return stack.getCount() == 1 && !stack.getItem().getRegistryName().toString().contains("sapling");
    }

    @Override
    public void tick() {
        Item pickup = this.getItemInHand(this.getUsedItemHand()).getItem();
        if (!hasLapis() && pickup == Items.LAPIS_LAZULI) {
            setLapis(true);
            this.setItemInHand(this.getUsedItemHand(), ItemStack.EMPTY);
        }
        if (hasLapis() && pickup.getRegistryName().toString().contains("log")) {
            setLapis(false);
            String sapling = pickup.getRegistryName().toString().replace("log", "sapling");
            for (Item item : ForgeRegistries.ITEMS) {
                if (item.getRegistryName().toString().equals(sapling)) {
                    this.level.addFreshEntity(new ItemEntity(this.level, this.getX() + 1, this.getY() + 0.5f, this.getZ(), item.getDefaultInstance()));
                    this.setItemInHand(this.getUsedItemHand(), ItemStack.EMPTY);
                }
            }

        }
        super.tick();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(HAS_LAPIS, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level, tag);
    }

    public boolean hasLapis() {
        return entityData.get(HAS_LAPIS);
    }

    public void setLapis(boolean lapis) {
        entityData.set(HAS_LAPIS, lapis);
    }

    @Override
    public void setRemainingPersistentAngerTime(int p_34448_) {
        this.remainingPersistentAngerTime = p_34448_;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));

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
        if (this.isOnGround() && !this.isAngry()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.model.idle", true));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    @Override
    public void performRangedAttack(LivingEntity entity, float index) {
        Snowball snowball = new Snowball(this.level, this);
        double d0 = entity.getEyeY() - (double) 1.1F;
        double d1 = entity.getX() - this.getX();
        double d2 = d0 - snowball.getY();
        double d3 = entity.getZ() - this.getZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double) 0.2F;
        snowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(snowball);
    }

    public static class SpiritTargetGoal extends TargetGoal {

        public SpiritTargetGoal(Mob p_26140_) {
            super(p_26140_, true);
        }

        @Override
        public boolean canUse() {
            return false;
        }

        @Override
        public void start() {
            this.targetMob = this.mob.getTarget();
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
        }
    }

    public static class FollowItemGoal extends Goal {
        private final Mob mob;
        private final Predicate<Mob> followPredicate;
        @javax.annotation.Nullable
        private ItemEntity followingMob;
        private final double speedModifier;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float stopDistance;
        private float oldWaterCost;
        private final float areaSize;

        public FollowItemGoal(Mob p_25271_, double p_25272_, float p_25273_, float p_25274_) {
            this.mob = p_25271_;
            this.followPredicate = (p_25278_) -> {
                return p_25278_ != null && p_25271_.getClass() != p_25278_.getClass();
            };
            this.speedModifier = p_25272_;
            this.navigation = p_25271_.getNavigation();
            this.stopDistance = p_25273_;
            this.areaSize = p_25274_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            List<ItemEntity> list = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(this.areaSize));
            if (!list.isEmpty()) {
                for (ItemEntity mob : list) {
                    if (!mob.isInvisible()) {
                            this.followingMob = mob;
                        return true;
                    }
                }
            }

            return false;
        }

        public boolean canContinueToUse() {
            return this.followingMob != null && !this.navigation.isDone() && this.mob.distanceToSqr(this.followingMob) > (double) (this.stopDistance * this.stopDistance);
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
            this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.followingMob = null;
            this.navigation.stop();
            this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            if (this.followingMob != null && !this.mob.isLeashed()) {
                this.mob.getLookControl().setLookAt(this.followingMob, 10.0F, (float) this.mob.getMaxHeadXRot());
                if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = this.adjustedTickDelay(10);
                    double d0 = this.mob.getX() - this.followingMob.getX();
                    double d1 = this.mob.getY() - this.followingMob.getY();
                    double d2 = this.mob.getZ() - this.followingMob.getZ();
                    double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                    if (!(d3 <= (double) (this.stopDistance * this.stopDistance))) {
                        this.navigation.moveTo(this.followingMob, this.speedModifier);
                    } else {
                        this.navigation.stop();
                    }
                }
            }
        }
    }
}
