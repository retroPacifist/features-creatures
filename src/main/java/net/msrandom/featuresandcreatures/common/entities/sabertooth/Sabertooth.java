package net.msrandom.featuresandcreatures.common.entities.sabertooth;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.msrandom.featuresandcreatures.common.entities.AbstractAngryEntity;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

public class Sabertooth extends AbstractAngryEntity implements IAngerable, IAnimatable {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.SALMON, Items.COD, Items.MUTTON);
    private final AnimationFactory factory = new AnimationFactory(this);


    public Sabertooth(EntityType<? extends Sabertooth> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 4.0F);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD_ITEMS.test(stack);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, false, FOOD_ITEMS));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SheepEntity.class, 10, true, true, null));
    }

    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? SoundEvents.POLAR_BEAR_AMBIENT_BABY : SoundEvents.POLAR_BEAR_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity p_241840_2_) {
        Sabertooth sabertooth = FnCEntities.SABERTOOTH.get().create(world);
        sabertooth.setAge(-24000);
        return sabertooth;
    }

    @Override
    public SoundEvent getWarningSound() {
        return SoundEvents.POLAR_BEAR_WARNING;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (this.isOnGround() && event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.sabertooth.walk", true));
            return PlayState.CONTINUE;
        } else if (this.isStanding()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.sabertooth.attack", true));
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
}
