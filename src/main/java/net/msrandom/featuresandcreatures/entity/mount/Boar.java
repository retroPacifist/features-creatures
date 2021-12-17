package net.msrandom.featuresandcreatures.entity.mount;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.List;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createEntity;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class Boar extends AbstractAngryMountEntity implements IRideable {
    private static final DataParameter<Integer> BOOST_TIME = EntityDataManager.defineId(Boar.class, DataSerializers.INT);
    private static final SoundsProvider SOUNDS_PROVIDER = SoundsProvider.create(
            FnCSounds.BOAR_AMBIENT,
            FnCSounds.BOAR_HURT,
            FnCSounds.BOAR_DEATH,
            FnCSounds.BOAR_SADDLE);
    private static final Ingredient FOODS = Ingredient.of(Items.CARROT);
    private static final String WALK_ANIMATION = "animation.boar.walk";
    private static final String ATTACK_ANIMATION = "animation.boar.attack";

    private final BoostHelper boostHelper = new BoostHelper(entityData, BOOST_TIME, SADDLED);

    public Boar(EntityType<? extends Boar> entityType, World world) {
        super(entityType, world);
    }

    public static @NotNull AttributeModifierMap.MutableAttribute createBoarAttributes() {
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
    public void onSyncedDataUpdated(DataParameter<?> dataParameter) {
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
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        setBoostTime(compoundNBT.getInt("BoostTime"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("BoostTime", getBoostTime());
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity playerEntity, Hand hand) {
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
    public @Nullable AgeableEntity getBreedOffspring(ServerWorld serverWorld, AgeableEntity entity) {
        return createEntity(FnCEntities.BOAR, serverWorld, boarEntity -> boarEntity.setAge(-24000));
    }

    @Override
    protected double getBreedWalkSpeed() {
        return 0.8D;
    }

    @Override
    public boolean canBeControlledByRider() {
        Entity entity = getControllingPassenger();
        if (!(entity instanceof PlayerEntity)) {
            return false;
        } else {
            PlayerEntity playerentity = (PlayerEntity) entity;
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
    public void travel(Vector3d vector3d) {
        travel(this, boostHelper, vector3d);
    }

    @Override
    public void travelWithInput(Vector3d vector3d) {
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
