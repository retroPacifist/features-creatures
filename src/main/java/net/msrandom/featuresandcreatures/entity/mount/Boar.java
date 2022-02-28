package net.msrandom.featuresandcreatures.entity.mount;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createEntity;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class Boar extends AbstractAngryMountEntity implements PlayerRideable, ItemSteerable {
    private static final EntityDataAccessor<Integer> BOOST_TIME = SynchedEntityData.defineId(Boar.class, EntityDataSerializers.INT);
    private static final SoundsProvider SOUNDS_PROVIDER = SoundsProvider.create(
            FnCSounds.BOAR_AMBIENT,
            FnCSounds.BOAR_HURT,
            FnCSounds.BOAR_DEATH,
            FnCSounds.BOAR_SADDLE);
    private static final Ingredient FOODS = Ingredient.of(Items.CARROT);
    private static final String WALK_ANIMATION = "animation.boar.walk";
    private static final String ATTACK_ANIMATION = "animation.boar.attack";

    private final ItemBasedSteering boostHelper = new ItemBasedSteering(entityData, BOOST_TIME, SADDLED);

    public Boar(EntityType<? extends Boar> entityType, Level world) {
        super(entityType, world);
    }

    public static @NotNull AttributeSupplier.Builder createBoarAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 11.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BOOST_TIME, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataParameter) {
        if (BOOST_TIME.equals(dataParameter)) {
            boostHelper.onSynced();
        }
        super.onSyncedDataUpdated(dataParameter);
    }

    @Override
    protected void registerAdditionalGoals() {
        goalSelector.addGoal(3, new TemptGoal(this, 1.2D, Ingredient.of(Items.CARROT_ON_A_STICK), false));
        goalSelector.addGoal(2, new PanicGoal(this, 1.42D));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        setBoostTime(compoundNBT.getInt("BoostTime"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("BoostTime", getBoostTime());
    }

    @Override
    public InteractionResult mobInteract(Player playerEntity, InteractionHand hand) {
        if (!playerEntity.isCrouching() && !isFood(playerEntity.getItemInHand(hand)) && isSaddled() && !isVehicle() && !playerEntity.isSecondaryUseActive()) {
            return sidedOperation(() -> playerEntity.startRiding(this));
        }
        return super.mobInteract(playerEntity, hand);
    }

    @Override
    public @NotNull Ingredient getFoods() {
        return FOODS;
    }

    @Override
    protected @NotNull String getWalkAnimation() {
        return WALK_ANIMATION;
    }

    @Override
    protected @NotNull String getAttackAnimation() {
        return ATTACK_ANIMATION;
    }

    @Override
    protected @NotNull SoundsProvider getSoundsProvider() {
        return SOUNDS_PROVIDER;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        playSound(FnCSounds.BOAR_STEP, 0.15F, 1.0F);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob entity) {
        return createEntity(FnCEntities.BOAR, serverWorld, boarEntity -> boarEntity.setAge(-24000));
    }

    @Override
    protected double getBreedWalkSpeed() {
        return 0.8D;
    }

    @Override
    public boolean canBeControlledByRider() {
        Entity entity = getControllingPassenger();
        if (!(entity instanceof Player)) {
            return false;
        } else {
            Player playerentity = (Player) entity;
            return playerentity.getMainHandItem().getItem() == Items.CARROT_ON_A_STICK || playerentity.getOffhandItem().getItem() == Items.CARROT_ON_A_STICK;
        }
    }

    @Override
    public @Nullable Entity getControllingPassenger() {
        List<Entity> passengers = getPassengers();
        return passengers.isEmpty() ? null : passengers.get(0);
    }

    // IRideable
    @Override
    public boolean boost() {
        return boostHelper.boost(getRandom());
    }

    @Override
    public void travel(Vec3 vector3d) {
        this.travel(this, boostHelper, vector3d);
    }

    @Override
    public void travelWithInput(Vec3 vector3d) {
        super.travel(vector3d);
    }


    @Override
    public float getSteeringSpeed() {
        return (float) (getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225F);
    }

    public int getBoostTime() {
        return entityData.get(BOOST_TIME);
    }

    public void setBoostTime(int i) {
        entityData.set(BOOST_TIME, i);
    }
}
