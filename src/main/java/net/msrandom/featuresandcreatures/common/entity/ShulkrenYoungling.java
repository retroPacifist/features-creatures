package net.msrandom.featuresandcreatures.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.core.FnCTags;
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
        READY, ADMIRING, OFFERING, EATING, SOUP;

        public static final Phase[] VALUES = values();

        public static Phase byIndex(int index) {
            return index >= 0 && index < VALUES.length ? VALUES[index] : READY;
        }
    }

    private static final int SAPLING_RADIUS = 4;
    private static final int SAPLING_TRIES = 25;

    private static final int TIME_ADMIRE = 200; // 20 seconds
    private static final int TIME_OFFER = 1200; // 1 minute
    private static final int TIME_SOUP = 6000; // 5 minutes
    private static final int TIME_EAT = 100; // 5 seconds
    private static final int TIME_SAPLING_INTERVAL = 1200; // 1 minute
    private static final int TIME_REFUSE = 6000; // 5 minutes

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
        if (getTradePhase() == Phase.READY) {
            if (itemInHand.is(Items.ENDER_EYE)) {
                itemInHand.shrink(1);
                setItemInHand(InteractionHand.MAIN_HAND, Items.ENDER_EYE.getDefaultInstance());
                setTradePhase(Phase.ADMIRING);
                nextPhase = level.getGameTime() + TIME_ADMIRE;
                return InteractionResult.SUCCESS;
            } else if (itemInHand.is(Items.MUSHROOM_STEW)) {
                itemInHand.shrink(1);
                setTradePhase(Phase.EATING);
                nextPhase = level.getGameTime() + TIME_EAT;

                return InteractionResult.SUCCESS;
            }
        } else if (getTradePhase() == Phase.OFFERING) {
            swing(InteractionHand.OFF_HAND);
            BehaviorUtils.throwItem(this, getItemInHand(InteractionHand.MAIN_HAND), player.position().add(0.0D, 1.0D, 0.0D));
            setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            setTradePhase(Phase.READY);
            return InteractionResult.SUCCESS;
        }
        if (itemInHand.is(Items.ENDER_EYE)) {
            return InteractionResult.CONSUME; // to avoid accidentally wasting eyes
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new TryFollowPlayerGoal(this, 16));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        goalSelector.addGoal(2, new TemptGoal(this, 1F, Ingredient.of(Items.ENDER_EYE), false));
        goalSelector.addGoal(1, new PanicGoal(this, 2D));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(tradePhase, (byte)0); // READY
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide()) return;
        long time = level.getGameTime();
        // intentional fallthrough in case enough time has passed while unloaded
        switch (getTradePhase()) {
            case READY: break;
            case ADMIRING:
                if (nextPhase <= time) {
                    List<ItemStack> response = getBarterResponseItems();
                    if (response.isEmpty()) {
                        setItemInHand(InteractionHand.MAIN_HAND, Items.DIAMOND.getDefaultInstance());
                    } else {
                        setItemInHand(InteractionHand.MAIN_HAND, response.get(random.nextInt(response.size())));
                    }
                    setTradePhase(Phase.OFFERING);
                    nextPhase += TIME_OFFER;
                }
            case OFFERING:
                if (nextPhase <= time) {
                    setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    setTradePhase(Phase.READY);
                }
                break;
            case EATING:
                if (nextPhase <= time) {
                    Vec2 rot = getRotationVector();
                    BehaviorUtils.throwItem(this, Items.BOWL.getDefaultInstance(), position().add(rot.x, 1.0, rot.y));
                    setTradePhase(Phase.SOUP);
                    nextPhase += TIME_SOUP;
                }
                break;
            case SOUP:
                if (nextPhase <= time) {
                    setTradePhase(Phase.READY);
                } else if ((nextPhase - time) % TIME_SAPLING_INTERVAL == 0) {
                    boolean placed = false;

                    Optional<Block> optionalSapling = FnCTags.Blocks.getRandomElement(FnCTags.Blocks.SHULKREN_YOUNGLING_SAPLINGS, random);
                    if (optionalSapling.isPresent())
                    {
                        BlockState sapling = optionalSapling.get().defaultBlockState();
                        //TODO: line-of-sight check?
                        Predicate<BlockPos> canPlant = pos -> !pos.equals(blockPosition()) && level.getBlockState(pos).isAir() && sapling.canSurvive(level, pos);
                        // first, try randomly
                        for (BlockPos pos : BlockPos.randomInCube(random, SAPLING_TRIES, blockPosition(), SAPLING_RADIUS)) {
                            if (canPlant.test(pos)) {
                                level.setBlock(pos, sapling, Block.UPDATE_CLIENTS);
                                placed = true;
                                break;
                            }
                        }
                        // next, try nearest
                        if (!placed) {
                            Optional<BlockPos> newpos = BlockPos.findClosestMatch(blockPosition(), SAPLING_RADIUS, SAPLING_RADIUS, canPlant);
                            if (newpos.isPresent()) {
                                level.setBlock(newpos.get(), sapling, Block.UPDATE_CLIENTS);
                                placed = true;
                            }
                        }
                        if (placed) {
                            swing(InteractionHand.OFF_HAND);
                        } else {
                            Vec2 rot = getRotationVector();
                            Item saplingItem = sapling.getBlock().asItem();
                            if (saplingItem != Items.AIR) {
                                BehaviorUtils.throwItem(this, Items.OAK_SAPLING.getDefaultInstance(), position().add(rot.x, 1.0, rot.y));
                            }
                        }
                    }

                }
        }
    }

    private List<ItemStack> getBarterResponseItems() {
        MinecraftServer server = level.getServer();
        if (server == null) return List.of(Items.ENDER_PEARL.getDefaultInstance());
        LootTable loottable = server.getLootTables().get(new ResourceLocation(FeaturesAndCreatures.MOD_ID, "gameplay/shulkren_bartering"));
        return loottable.getRandomItems((new LootContext.Builder((ServerLevel)level)).withParameter(LootContextParams.THIS_ENTITY, this).withRandom(level.random).create(LootContextParamSets.PIGLIN_BARTER));
    }

    @Override
    public boolean canPickUpLoot() {
        return getTradePhase() == Phase.READY;
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return stack.getCount() == 1 && stack.is(Items.ENDER_EYE);
    }

    @Override
    public void pickUpItem(ItemEntity item) {
        super.pickUpItem(item);
        // can only be ENDER_EYE so no need to check again
        setTradePhase(Phase.ADMIRING);
        nextPhase = level.getGameTime() + TIME_ADMIRE;
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (source instanceof EntityDamageSource entity && entity.getEntity() instanceof Player) {
            nextPhase = level.getGameTime() + TIME_REFUSE;
            level.getEntitiesOfClass(ShulkrenYoungling.class, new AABB(blockPosition()).inflate(16)).forEach(youngling -> {
                youngling.nextPhase = level.getGameTime() + TIME_REFUSE;
            });
        }
        return super.hurt(source, amount);
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
        Phase phase = getTradePhase();
        if (phase == Phase.ADMIRING || phase == Phase.OFFERING) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.hold", true));
            return PlayState.CONTINUE;
        }
        else if (phase == Phase.EATING) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.shulkren_youngling.eat"));
            return PlayState.CONTINUE;
        }
        if (isOnGround()) {
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
        return Phase.byIndex(entityData.get(tradePhase));
    }

    public void setTradePhase(Phase phase) {
        entityData.set(tradePhase, (byte) phase.ordinal());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("TradePhase", entityData.get(tradePhase));
        nbt.putLong("NextPhase", nextPhase);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        entityData.set(tradePhase, nbt.getByte("TradePhase"));
        nextPhase = nbt.getLong("NextPhase");
    }

    static class TryFollowPlayerGoal extends Goal {
        private final ShulkrenYoungling mob;
        private final int distance;
        private Player target = null;
        private int pathRecalcDelay = 0;

        TryFollowPlayerGoal(ShulkrenYoungling mob, int distance) {
            this.mob = mob;
            this.distance = distance;
        }

        @Override
        public boolean canUse() {
            if (mob.getTradePhase() == Phase.OFFERING) {
                target = mob.level.getNearestPlayer(mob, distance);
                return target != null;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return mob.getTradePhase() == Phase.OFFERING && target != null && target.isAlive() && target.distanceToSqr(target) < distance * distance;
        }

        @Override
        public void start() {
            pathRecalcDelay = 0;

        }

        @Override
        public void tick() {
            mob.getLookControl().setLookAt(target, 10.0F, mob.getMaxHeadXRot());
            if (--pathRecalcDelay <= 0) {
                pathRecalcDelay = adjustedTickDelay(10);
                if (!mob.isLeashed() && !mob.isPassenger()) {
                    mob.getNavigation().moveTo(target, 1f);
                }
            }
        }

        @Override
        public void stop() {
            mob.getNavigation().stop();
            target = null;
        }
    }
}