package net.msrandom.featuresandcreatures.entity.mount;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractMountEntity extends AbstractSoundsProviderEntity implements IAnimatable {
    protected static final DataParameter<Integer> ANIMATION_TIME = EntityDataManager.defineId(AbstractMountEntity.class, DataSerializers.INT);
    protected static final DataParameter<Boolean> SADDLED = EntityDataManager.defineId(AbstractMountEntity.class, DataSerializers.BOOLEAN);

    protected final AnimationFactory animationFactory = new AnimationFactory(this);

    protected AbstractMountEntity(EntityType<? extends AbstractMountEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new BreedGoal(this, getBreedWalkSpeed()));
        goalSelector.addGoal(2, new FollowParentGoal(this, 1.25D));
        goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        goalSelector.addGoal(4, new TemptGoal(this, 1.2D, false, getFoods()));
        goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        goalSelector.addGoal(5, new LookRandomlyGoal(this));
        registerAdditionalGoals();
    }

    protected abstract void registerAdditionalGoals();

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ANIMATION_TIME, 0);
        entityData.define(SADDLED, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        setAnimationTime(compoundNBT.getInt("AnimationTime"));
        setSaddled(compoundNBT.getBoolean("Saddled"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("AnimationTime", getAnimationTime());
        compoundNBT.putBoolean("Saddled", isSaddled());
    }

    @Override
    public float getScale() {
        return isBaby() ? 0.825F : 1.0F;
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity playerEntity, Hand hand) {
        if (!isBaby()) {
            if (!isSaddled()) {
                ItemStack itemStack = playerEntity.getItemInHand(hand);
                if (itemStack.getItem() == Items.SADDLE) {
                    return sidedOperation(() -> {
                        playSaddledSound(getSoundsProvider().saddled);
                        setSaddled(true);
                        if (!playerEntity.abilities.instabuild) {
                            itemStack.shrink(1);
                        }
                    });
                }
            } else if (playerEntity.isCrouching()) {
                return sidedOperation(() -> {
                    playSaddledSound(FnCSounds.ENTITY_DESADDLE);
                    ItemStack itemStack1 = new ItemStack(Items.SADDLE);
                    if (!playerEntity.inventory.add(itemStack1)) {
                        playerEntity.drop(itemStack1, false);
                    }
                    setSaddled(false);
                });
            }
        }
        return super.mobInteract(playerEntity, hand);
    }

    protected void playSaddledSound(SoundEvent soundEvent) {
        level.playSound(null, getX(), getY() + getDimensions(getPose()).height / 3.0D, getZ(), soundEvent, getSoundSource(), 1, 1);
    }

    protected ActionResultType sidedOperation(Runnable runnable) {
        if (!level.isClientSide) {
            runnable.run();
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return getFoods().test(itemStack);
    }

    public abstract @NotNull Ingredient getFoods();

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::getPlayState));
    }

    @SuppressWarnings("NullableProblems")
    protected abstract <T extends IAnimatable> PlayState getPlayState(AnimationEvent<T> event);

    @Override
    public AnimationFactory getFactory() {
        return animationFactory;
    }

    public int getAnimationTime() {
        return entityData.get(ANIMATION_TIME);
    }

    public boolean isSaddled() {
        return entityData.get(SADDLED);
    }

    public void setAnimationTime(int i) {
        entityData.set(ANIMATION_TIME, i);
    }

    public void setSaddled(boolean saddled) {
        entityData.set(SADDLED, saddled);
    }

    protected double getBreedWalkSpeed() {
        return 1.25D;
    }
}
