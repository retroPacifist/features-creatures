package net.msrandom.featuresandcreatures.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public abstract class AbstractMountEntity extends Animal implements IAnimatable {
    private static final DataParameter<Boolean> SADDLED = EntityDataManager.defineId(AbstractMountEntity.class, DataSerializers.BOOLEAN);

    protected AbstractMountEntity(EntityType<? extends AbstractMountEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.32D));
        goalSelector.addGoal(2, new FollowParentGoal(this, 1.25D));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SADDLED, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        setSaddled(compoundNBT.getBoolean("Saddled"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("Saddled", isSaddled());
    }

    @Override
    public InteractionResult mobInteract(Player playerEntity, InteractionHand hand) {
        if (!isBaby()) {
            if (!isSaddled()) {
                ItemStack itemStack = playerEntity.getItemInHand(hand);
                if (itemStack.getItem() == Items.SADDLE) {
                    return sidedOperation(serverWorld -> {
                        setSaddled(true);
                        if (!playerEntity.isCreative()) {
                            itemStack.shrink(1);
                        }
                    });
                }
            } else if (playerEntity.isCrouching()) {
                return sidedOperation(serverWorld -> {
                    ItemStack itemStack1 = new ItemStack(Items.SADDLE);
                    if (!playerEntity.getInventory().add(itemStack1)) {
                        playerEntity.drop(itemStack1, false);
                    }
                    setSaddled(false);
                });
            }
        }
        return super.mobInteract(playerEntity, hand);
    }

    private ActionResultType sidedOperation(Consumer<ServerLevel> consumer) {
        if (!level.isClientSide) {
            consumer.accept((ServerLevel) level);
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return super.isFood(itemStack);
    }

    public abstract @NotNull Ingredient getFoods();

    @Override
    public float getScale() {
        float scale = super.getScale();
        return isBaby() ? scale * 1.75F : scale;
    }

    public boolean isSaddled() {
        return entityData.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        entityData.set(SADDLED, saddled);
    }
}
