package net.msrandom.featuresandcreatures.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ShulkrenYoungling extends PathfinderMob implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    private static final EntityDataAccessor<Integer> tradeTimer = SynchedEntityData.defineId(ShulkrenYoungling.class, EntityDataSerializers.INT);


    public ShulkrenYoungling(EntityType<? extends ShulkrenYoungling> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public static boolean checkSpawnRules(EntityType<ShulkrenYoungling> type, LevelAccessor world, MobSpawnType spawnType, BlockPos pos, Random random) {
        if (world.getBlockState(pos.below()).is(Blocks.END_STONE)) {
            return checkMobSpawnRules(type, world, spawnType, pos, random);
        }
        return false;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand == Items.ENDER_EYE.getDefaultInstance()) {
            itemInHand.shrink(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, itemInHand);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1F, Ingredient.of(Items.ENDER_EYE), false));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(tradeTimer, 100);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isHolding(Items.ENDER_EYE)){
            if (!(this.getTarget() instanceof Player)) {
                this.setTradeTimer(this.getTradeTimer() - 1);
            }
        }
        if (this.getTradeTimer() <= 0){
            this.setDeltaMovement(this.getDeltaMovement());
            this.setItemInHand(this.getUsedItemHand(), ItemStack.EMPTY);
            Player nearestPlayer = this.level.getNearestPlayer(this, 50);
            throwItemsTowardPos(this, getBarterResponseItems(this), Objects.requireNonNullElse(nearestPlayer, this).position());
            this.setTradeTimer(100);
        }
    }

    private static List<ItemStack> getBarterResponseItems(ShulkrenYoungling youngling) {
        MinecraftServer server = youngling.level.getServer();
        if (server == null) return List.of(Items.ENDER_PEARL.getDefaultInstance());
        LootTable loottable = server.getLootTables().get(new ResourceLocation(FeaturesAndCreatures.MOD_ID, "gameplay/shulkren_bartering"));
        return loottable.getRandomItems((new LootContext.Builder((ServerLevel)youngling.level)).withParameter(LootContextParams.THIS_ENTITY, youngling).withRandom(youngling.level.random).create(LootContextParamSets.PIGLIN_BARTER));
    }

    private static void throwItemsTowardPos(ShulkrenYoungling youngling, List<ItemStack> items, Vec3 vec3) {
        if (!items.isEmpty()) {
            youngling.swing(InteractionHand.OFF_HAND);

            for(ItemStack itemstack : items) {
                BehaviorUtils.throwItem(youngling, itemstack, vec3.add(0.0D, 1.0D, 0.0D));
            }
        }
    }

    @Override
    public boolean canPickUpLoot() {
        return true;
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return stack.getCount() == 1 && stack.getItem() == Items.ENDER_EYE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0).add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (!this.isHolding(Items.ENDER_EYE) && this.isOnGround() && !event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.idle", true));
            return PlayState.CONTINUE;
        }
        if (!this.isHolding(Items.ENDER_EYE) && this.isOnGround() && event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.walk", true));
            return PlayState.CONTINUE;
        }
        if (this.isHolding(Items.ENDER_EYE)){
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.hold", true));
            return PlayState.CONTINUE;
        }
        else{
            return PlayState.STOP;
        }
    }

    public int getTradeTimer() {
        return entityData.get(tradeTimer);
    }

    public void setTradeTimer(int timer) {
        entityData.set(tradeTimer, timer);
    }
}
