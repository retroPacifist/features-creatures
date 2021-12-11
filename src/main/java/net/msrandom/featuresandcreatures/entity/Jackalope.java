package net.msrandom.featuresandcreatures.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

public class Jackalope extends AnimalEntity implements AgingMountEntity {

    private static final DataParameter<Boolean> SADDLED = EntityDataManager.defineId(Jackalope.class, DataSerializers.BOOLEAN);
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CARROT, Items.GOLDEN_CARROT);
    private final AnimationFactory factory = new AnimationFactory(this);

    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int jumpDelayTicks;

    public Jackalope(EntityType<? extends Jackalope> type, World world) {
        super(type, world);
        this.jumpControl = new JumpHelperController(this);
        this.moveControl = new MoveHelperController(this);
        this.setSpeedModifier(0.0D);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.7F);
    }
    @Override
    public void setBaby(boolean p_82227_1_) {
        super.setBaby(p_82227_1_);
        this.setBoundingBox(new AxisAlignedBB(this.blockPosition()));
    }


    protected float getJumpPower() {
        if (!this.horizontalCollision && (!this.moveControl.hasWanted() || !(this.moveControl.getWantedY() > this.getY() + 0.7D))) {
            Path path = this.navigation.getPath();
            if (path != null && !path.isDone()) {
                Vector3d vector3d = path.getNextEntityPos(this);
                if (vector3d.y > this.getY() + 0.6D) {
                    return 0.6F;
                }
            }
            return this.moveControl.getSpeedModifier() <= 0.6D ? 0.2F : 0.3F;
        } else {
            return 0.7F;
        }
    }

    protected void jumpFromGround() {
        super.jumpFromGround();
        double d0 = this.moveControl.getSpeedModifier();
        if (d0 > 0.0D) {
            double d1 = getHorizontalDistanceSqr(this.getDeltaMovement());
            if (d1 < 0.01D) {
                this.moveRelative(0.2F, new Vector3d(0.0D, 0.0D, 1.0D));
            }
        }
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte) 1);
        }
    }

    public void setSpeedModifier(double p_175515_1_) {
        this.getNavigation().setSpeedModifier(p_175515_1_);
        this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), p_175515_1_);
    }

    public void setJumping(boolean isJumping) {
        super.setJumping(isJumping);
        if (isJumping) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled())
            this.spawnAtLocation(Items.SADDLE);
    }

    public void startJumping() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    public void customServerAiStep() {
        if (this.jumpDelayTicks > 0) {
            --this.jumpDelayTicks;
        }

        if (this.onGround) {
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            JumpHelperController controller = (JumpHelperController) this.jumpControl;
            if (!controller.wantJump()) {
                if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
                    Path path = this.navigation.getPath();
                    Vector3d vector3d = new Vector3d(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
                    if (path != null && !path.isDone()) {
                        vector3d = path.getNextEntityPos(this);
                    }

                    this.facePoint(vector3d.x, vector3d.z);
                    this.startJumping();
                }
            } else if (!controller.canJump()) {
                this.enableJumpControl();
            }
        }
        this.wasOnGround = this.onGround;
    }

    public boolean canSpawnSprintParticle() {
        return false;
    }

    private void facePoint(double p_175533_1_, double p_175533_3_) {
        this.yRot = (float) (MathHelper.atan2(p_175533_3_ - this.getZ(), p_175533_1_ - this.getX()) * (double) (180F / (float) Math.PI)) - 90.0F;
    }

    private void enableJumpControl() {
        ((JumpHelperController) this.jumpControl).setCanJump(true);
    }

    private void disableJumpControl() {
        ((JumpHelperController) this.jumpControl).setCanJump(false);
    }

    private void setLandingDelay() {
        if (this.moveControl.getSpeedModifier() < 2.2D) {
            this.jumpDelayTicks = 10;
        } else {
            this.jumpDelayTicks = 1;
        }
    }

    private void checkLandingDelay() {
        this.setLandingDelay();
        this.disableJumpControl();
    }

    public void aiStep() {
        super.aiStep();
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.RABBIT_JUMP;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 1) {
            this.spawnSprintParticle();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else {
            super.handleEntityEvent(p_70103_1_);
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD_ITEMS.test(stack);
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity entity) {
        Jackalope jackalope = FnCEntities.JACKALOPE.get().create(world);
        jackalope.setAge(-24000);
        return jackalope;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SADDLED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.25D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.of(Items.CARROT), false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        super.mobInteract(player, hand);
        if (player.isHolding(Items.SADDLE)) {
            this.setSaddled(true);
            if (!player.isCreative()) {
                player.getItemInHand(hand).shrink(1);
            }
        }
        if (player.isCrouching() && player.getItemInHand(hand).getItem() != Items.SADDLE && this.isSaddled()) {
            this.setSaddled(false);
            player.level.addFreshEntity(new ItemEntity(player.level, this.getX(), this.getY() + 0.3f, this.getZ(), Items.SADDLE.getDefaultInstance()));
            player.level.playSound(null, this.getX(), this.getY() + 0.3f, this.getZ(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.AMBIENT, 1, 1);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        this.setSaddled(compoundNBT.getBoolean("Saddled"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("Saddled", this.isSaddled());
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;

        if (this.jumpDuration >= 1) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jackalope.walk", true));
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

    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, saddled);
    }

    static class MoveHelperController extends MovementController {
        private final Jackalope jack;
        private double nextJumpSpeed;

        public MoveHelperController(Jackalope jackalope) {
            super(jackalope);
            this.jack = jackalope;
        }

        public void tick() {
            if (this.jack.onGround && !this.jack.jumping && !((JumpHelperController) this.jack.jumpControl).wantJump()) {
                this.jack.setSpeedModifier(0.0D);
            } else if (this.hasWanted()) {
                this.jack.setSpeedModifier(this.nextJumpSpeed);
            }
            super.tick();
        }

        public void setWantedPosition(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_) {
            if (this.jack.isInWater()) {
                p_75642_7_ = 1.5D;
            }
            super.setWantedPosition(p_75642_1_, p_75642_3_, p_75642_5_, p_75642_7_);
            if (p_75642_7_ > 0.0D) {
                this.nextJumpSpeed = p_75642_7_;
            }
        }
    }

    public class JumpHelperController extends JumpController {
        private final Jackalope jack;
        private boolean canJump;

        public JumpHelperController(Jackalope jackalope) {
            super(jackalope);
            this.jack = jackalope;
        }

        public boolean wantJump() {
            return this.jump;
        }

        public boolean canJump() {
            return this.canJump;
        }

        public void setCanJump(boolean p_180066_1_) {
            this.canJump = p_180066_1_;
        }

        public void tick() {
            if (this.jump) {
                this.jack.startJumping();
                this.jump = false;
            }
        }
    }
}
