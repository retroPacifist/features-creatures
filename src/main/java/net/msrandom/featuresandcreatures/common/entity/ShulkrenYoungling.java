package net.msrandom.featuresandcreatures.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec2;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class ShulkrenYoungling extends PathfinderMob implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    private enum Phase {
        READY, ADMIRING, OFFERING, SOUP;
        public static final List<Phase> list = List.of(READY, ADMIRING, OFFERING, SOUP);
    }

    //TODO: mod saplings
    private static final SaplingBlock[] SAPLINGS = {(SaplingBlock) Blocks.ACACIA_SAPLING, (SaplingBlock) Blocks.BIRCH_SAPLING, (SaplingBlock) Blocks.DARK_OAK_SAPLING, (SaplingBlock) Blocks.JUNGLE_SAPLING, (SaplingBlock) Blocks.OAK_SAPLING};
    private static final int SAPLING_RADIUS = 4;
    private static final int SAPLING_TRIES = 25;

    private static final int TIME_ADMIRE = 200; // 20 seconds
    private static final int TIME_OFFER = 1200; // 1 minute
    private static final int TIME_SOUP = 6000; // 5 minutes
    private static final int TIME_SAPLING_INTERVAL = 1200; // 1 minute

    private static final EntityDataAccessor<Byte> tradePhase = SynchedEntityData.defineId(ShulkrenYoungling.class, EntityDataSerializers.BYTE);
    private long nextPhase = 0;
    
    public ShulkrenYoungling(EntityType<? extends ShulkrenYoungling> type, Level level) {
        super(type, level);
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
        if (this.getTradePhase() == Phase.READY) {
            if (itemInHand.is(Items.ENDER_EYE)) {
                itemInHand.shrink(1);
                this.setItemInHand(InteractionHand.MAIN_HAND, Items.ENDER_EYE.getDefaultInstance());
                this.setTradePhase(Phase.ADMIRING);
                this.nextPhase = this.level.getGameTime() + TIME_ADMIRE;
                return InteractionResult.SUCCESS;
            } else if (itemInHand.is(Items.MUSHROOM_STEW)) {
                itemInHand.shrink(1);
                this.setTradePhase(Phase.SOUP);
                this.nextPhase = this.level.getGameTime() + TIME_SOUP;
                Vec2 rot = this.getRotationVector();
                BehaviorUtils.throwItem(this, Items.BOWL.getDefaultInstance(), this.position().add(rot.x, 1.0, rot.y));
                return InteractionResult.SUCCESS;
            }
        } else if (this.getTradePhase() == Phase.OFFERING) {
            this.swing(InteractionHand.OFF_HAND);
            BehaviorUtils.throwItem(this, this.getItemInHand(InteractionHand.MAIN_HAND), player.position().add(0.0D, 1.0D, 0.0D));
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            this.setTradePhase(Phase.READY);
            return InteractionResult.SUCCESS;
        }
        if (itemInHand.is(Items.ENDER_EYE)) {
            return InteractionResult.CONSUME; // to avoid accidentally wasting eyes
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
        entityData.define(tradePhase, (byte)0); // READY
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) return;
        long time = this.level.getGameTime();
        // intentional fallthrough in case enough time has passed while unloaded
        switch (this.getTradePhase()) {
            case READY: break;
            case ADMIRING:
                if (this.nextPhase <= time) {
                    this.setItemInHand(InteractionHand.MAIN_HAND, Items.DIAMOND.getDefaultInstance());
                    this.setTradePhase(Phase.OFFERING);
                    this.nextPhase += TIME_OFFER;
                }
            case OFFERING:
                if (this.nextPhase <= time) {
                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    this.setTradePhase(Phase.READY);
                }
                break;
            case SOUP:
                if (this.nextPhase <= time) {
                    this.setTradePhase(Phase.READY);
                } else if ((this.nextPhase - time) % TIME_SAPLING_INTERVAL == 0) {
                    boolean placed = false;
                    SaplingBlock sapling = SAPLINGS[random.nextInt(SAPLINGS.length)];
                    //TODO: line-of-sight check?
                    Predicate<BlockPos> canPlant = (BlockPos pos) -> pos != this.blockPosition() && level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).canSustainPlant(level, pos, Direction.UP, sapling);
                    // first, try randomly
                    for (BlockPos pos : BlockPos.randomInCube(random, SAPLING_TRIES, this.blockPosition(), SAPLING_RADIUS)) {
                        if (canPlant.test(pos)) {
                            level.setBlock(pos, sapling.defaultBlockState(), Block.UPDATE_CLIENTS);
                            placed = true;
                            break;
                        }
                    }
                    // next, try nearest
                    if (!placed) {
                        Optional<BlockPos> newpos = BlockPos.findClosestMatch(this.blockPosition(), SAPLING_RADIUS, SAPLING_RADIUS, canPlant);
                        if (newpos.isPresent()) {
                            level.setBlock(newpos.get(), sapling.defaultBlockState(), Block.UPDATE_CLIENTS);
                            placed = true;
                        }
                    }
                    if (placed) {
                        this.swing(InteractionHand.OFF_HAND);
                    } else {
                        Vec2 rot = this.getRotationVector();
                        BehaviorUtils.throwItem(this, Items.OAK_SAPLING.getDefaultInstance(), this.position().add(rot.x, 1.0, rot.y));
                    }
                }
        }
    }

    private List<ItemStack> getBarterResponseItems() {
        MinecraftServer server = this.level.getServer();
        if (server == null) return List.of(Items.ENDER_PEARL.getDefaultInstance());
        LootTable loottable = server.getLootTables().get(new ResourceLocation(FeaturesAndCreatures.MOD_ID, "gameplay/shulkren_bartering"));
        return loottable.getRandomItems((new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.THIS_ENTITY, this).withRandom(this.level.random).create(LootContextParamSets.PIGLIN_BARTER));
    }

    @Override
    public boolean canPickUpLoot() {
        return this.getTradePhase() == Phase.READY;
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return stack.getCount() == 1 && stack.is(Items.ENDER_EYE);
    }

    @Override
    public void pickUpItem(ItemEntity item) {
        super.pickUpItem(item);
        // can only be ENDER_EYE so no need to check again
        this.setItemInHand(InteractionHand.MAIN_HAND, Items.ENDER_EYE.getDefaultInstance());
        this.setTradePhase(Phase.ADMIRING);
        this.nextPhase = this.level.getGameTime() + TIME_ADMIRE;
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
        Phase phase = this.getTradePhase();
        if (phase == Phase.ADMIRING || phase == Phase.OFFERING) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.hold", true));
            return PlayState.CONTINUE;
        }
        if (this.isOnGround()) {
            if (event.isMoving()) {
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.walk", true));
                return PlayState.CONTINUE;
            } else {
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.idle", true));
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    public Phase getTradePhase() {
        return Phase.list.get(entityData.get(tradePhase));
    }

    public void setTradePhase(Phase phase) {
        entityData.set(tradePhase, (byte)Phase.list.indexOf(phase));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("TradePhase", this.entityData.get(tradePhase));
        nbt.putLong("NextPhase", this.nextPhase);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(tradePhase, nbt.getByte("TradePhase"));
        this.nextPhase = nbt.getLong("NextPhase");
    }
}