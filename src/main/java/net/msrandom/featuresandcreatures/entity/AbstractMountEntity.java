package net.msrandom.featuresandcreatures.entity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractMountEntity extends AnimalEntity implements IAnimatable {
    private static final DataParameter<Boolean> SADDLED = EntityDataManager.defineId(AbstractMountEntity.class, DataSerializers.BOOLEAN);

    protected AbstractMountEntity(EntityType<? extends AbstractMountEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.32D));
        goalSelector.addGoal(2, new FollowParentGoal(this, 1.25D));
        goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        goalSelector.addGoal(5, new LookRandomlyGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SADDLED, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        setSaddled(compoundNBT.getBoolean("Saddled"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("Saddled", isSaddled());
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity playerEntity, Hand hand) {
        if (!isBaby()) {
            if (!isSaddled()) {
                ItemStack itemStack = playerEntity.getItemInHand(hand);
                if (itemStack.getItem() == Items.SADDLE) {
                    return sidedOperation(serverWorld -> {
                        setSaddled(true);
                        if (!playerEntity.abilities.instabuild) {
                            itemStack.shrink(1);
                        }
                    });
                }
            } else if (playerEntity.isCrouching()) {
                return sidedOperation(serverWorld -> {
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

    private ActionResultType sidedOperation(Consumer<ServerWorld> consumer) {
        if (!level.isClientSide) {
            consumer.accept((ServerWorld) level);
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
