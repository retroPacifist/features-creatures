package net.msrandom.featuresandcreatures.common.entities.jackalope;

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
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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

public class Jackalope extends AnimalEntity implements IAnimatable {

    private static final DataParameter<Boolean> SADDLED = EntityDataManager.defineId(Jackalope.class, DataSerializers.BOOLEAN);
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CARROT, Items.GOLDEN_CARROT);
    private final AnimationFactory factory = new AnimationFactory(this);

    public Jackalope(EntityType<? extends Jackalope> type, World world) {
        super(type, world);
//        this.jumpControl = new RabbitEntity.JumpHelperController(this);
//        this.moveControl = new RabbitEntity.MoveHelperController(this);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.6F);
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
        if (player.isCrouching() && player.getItemInHand(hand).getItem() != Items.SADDLE && this.isSaddled()){
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

        if (this.isOnGround() && event.isMoving()) {
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
//
//    public class JumpHelperController extends JumpController {
//        private final Jackalope jackalope;
//        private boolean canJump;
//
//        public JumpHelperController(Jackalope p_i45863_2_) {
//            super(p_i45863_2_);
//            this.jackalope = p_i45863_2_;
//        }
//
//        public boolean wantJump() {
//            return this.jump;
//        }
//
//        public boolean canJump() {
//            return this.canJump;
//        }
//
//        public void setCanJump(boolean p_180066_1_) {
//            this.canJump = p_180066_1_;
//        }
//
//        public void tick() {
//            if (this.jump) {
//                this.jackalope.startJumping();
//                this.jump = false;
//            }
//
//        }
//    }
//
//    static class MoveHelperController extends MovementController {
//        private final Jackalope rabbit;
//        private double nextJumpSpeed;
//
//        public MoveHelperController(Jackalope p_i45862_1_) {
//            super(p_i45862_1_);
//            this.rabbit = p_i45862_1_;
//        }
//
//        public void tick() {
//            if (this.rabbit.onGround && !this.rabbit.jumping && !((RabbitEntity.JumpHelperController)this.rabbit.jumpControl).wantJump()) {
//                this.rabbit.setSpeedModifier(0.0D);
//            } else if (this.hasWanted()) {
//                this.rabbit.setSpeedModifier(this.nextJumpSpeed);
//            }
//
//            super.tick();
//        }
//
//        public void setWantedPosition(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_) {
//            if (this.rabbit.isInWater()) {
//                p_75642_7_ = 1.5D;
//            }
//
//            super.setWantedPosition(p_75642_1_, p_75642_3_, p_75642_5_, p_75642_7_);
//            if (p_75642_7_ > 0.0D) {
//                this.nextJumpSpeed = p_75642_7_;
//            }
//
//        }
//    }


    boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    //getters/setters
    private void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, saddled);
    }
}
