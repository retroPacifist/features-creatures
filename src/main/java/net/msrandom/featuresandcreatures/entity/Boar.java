package net.msrandom.featuresandcreatures.entity;

import com.mojang.math.Vector3d;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Boar extends AbstractAngryEntity implements IAngerable, IAnimatable, PlayerRideable {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CARROT);
    private static final DataParameter<Boolean> DATA_SADDLE_ID = EntityDataManager.defineId(Boar.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> DATA_BOOST_TIME = EntityDataManager.defineId(Boar.class, DataSerializers.INT);
    private final AnimationFactory factory = new AnimationFactory(this);
    private final BoostHelper boostHelper = new BoostHelper(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
    public int animationTimer;


    public Boar(EntityType<? extends Boar> type, Level world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 11.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0F);
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundTag p_213386_5_) {
        this.animationTimer = 0;
        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
        if (DATA_BOOST_TIME.equals(p_184206_1_) && this.level.isClientSide) {
            this.boostHelper.onSynced();
        }

        super.onSyncedDataUpdated(p_184206_1_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SADDLE_ID, false);
        this.entityData.define(DATA_BOOST_TIME, 0);
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        this.boostHelper.addAdditionalSaveData(nbt);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.boostHelper.readAdditionalSaveData(nbt);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD_ITEMS.test(stack);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, false, FOOD_ITEMS));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.of(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.42D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
    }

    @Override
    public ActionResultType mobInteract(Player player, InteractionHand hand) {
        boolean flag = this.isFood(player.getItemInHand(hand));
        if (!flag && this.isSaddled() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level.isClientSide) {
                player.startRiding(this);
            }
            return ActionResultType.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    public boolean canBeControlledByRider() {
        Entity entity = this.getControllingPassenger();
        if (!(entity instanceof Player)) {
            return false;
        } else {
            Player playerentity = (Player) entity;
            return playerentity.getMainHandItem().getItem() == Items.CARROT_ON_A_STICK || playerentity.getOffhandItem().getItem() == Items.CARROT_ON_A_STICK;
        }
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
    public Boar getBreedOffspring(ServerLevel world, AgeableMob mate) {
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

    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    public void travel(Vector3d p_213352_1_) {
        this.travel(this, this.boostHelper, p_213352_1_);
    }

    public float getSteeringSpeed() {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225F;
    }

    public void travelWithInput(Vector3d p_230267_1_) {
        super.travel(p_230267_1_);
    }

    public boolean boost() {
        return this.boostHelper.boost(this.getRandom());
    }
}
