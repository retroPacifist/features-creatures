package net.msrandom.featuresandcreatures.common.entities.boar;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
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


public class Boar extends AbstractAngryEntity implements IAngerable, IAnimatable {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CARROT, Items.POTATO, Items.BEETROOT);
    private final AnimationFactory factory = new AnimationFactory(this);
    public int animationTimer;


    public Boar(EntityType<? extends Boar> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 11.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0F);
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        this.animationTimer = 0;
        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
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

    @Override
    public SoundEvent getWarningSound() {
        return SoundEvents.PIG_HURT;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isStanding()) {
            this.animationTimer = 15;
        }
        if (this.animationTimer > 0) {
            --this.animationTimer;
        }
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
        if (event.isMoving() && this.animationTimer <= 0) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.boar.walk", true));
            return PlayState.CONTINUE;
        } else if (this.animationTimer > 0) {
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


}
