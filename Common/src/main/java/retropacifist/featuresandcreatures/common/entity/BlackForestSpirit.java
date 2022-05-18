package retropacifist.featuresandcreatures.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public abstract class BlackForestSpirit extends Monster implements NeutralMob, RangedAttackMob, FloatingEntity {

    private static final EntityDataAccessor<Boolean> HAS_LAPIS = SynchedEntityData.defineId(BlackForestSpirit.class, EntityDataSerializers.BOOLEAN);

    private List<ItemEntity> followingItem;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;


    public BlackForestSpirit(EntityType<? extends BlackForestSpirit> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(4, new FollowItemGoal(this, 12, 2F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1F, Ingredient.of(Items.LAPIS_LAZULI), false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.15F).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public boolean canPickUpLoot() {
        return true;
    }

    //TODO: Make our own tag.
    @Override
    public boolean canHoldItem(ItemStack stack) {
        if (this.hasLapis()) {
            return stack.is(ItemTags.LOGS);
        } else {
            return stack.is(Items.LAPIS_LAZULI);
        }
    }

    public static boolean checkSpawnRules(EntityType<BlackForestSpirit> type, LevelAccessor world, MobSpawnType spawnType, BlockPos pos, Random random) {
        if (world.getDifficulty() != Difficulty.PEACEFUL) {
            if (world.getBiome(pos).is(Biomes.DARK_FOREST) && pos.getY() > 50 && pos.getY() < 70 && random.nextFloat() < 0.5F && 0.0F == world.getMoonBrightness()) {
                return checkMobSpawnRules(type, world, spawnType, pos, random);
            }

            if (!(world instanceof WorldGenLevel)) {
                return false;
            }

            if (world.getBiome(pos).is(Biomes.DARK_FOREST) && ((WorldGenLevel) world).getLevel().isThundering()) {
                return checkMobSpawnRules(type, world, spawnType, pos, random);
            }
        }
        return false;
    }

    @Override
    protected float getBlockSpeedFactor() {
        return 1.0F;
    }

    @Override
    public float getBlockJumpFactor() {
        return 1.0F;
    }

    @Override
    protected boolean onSoulSpeedBlock() {
        return false;
    }

    @Override
    public void die(DamageSource p_21014_) {
        ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), this.getMainHandItem());
        this.level.addFreshEntity(itementity);
        super.die(p_21014_);
    }

    @Override
    public void tick() {
        Item pickup = this.getItemInHand(this.getUsedItemHand()).getItem();
        if (!hasLapis() && pickup == Items.LAPIS_LAZULI) {
            int i = random.nextInt(10);
            this.getMainHandItem().shrink(1);
            if (i == 4) {
                setLapis(true);
            }
        }

        ResourceLocation pickUpResourceLocation = Registry.ITEM.getKey(pickup);
        if ((hasLapis() && this.getMainHandItem().getCount() > 0) || (!hasLapis() && pickUpResourceLocation.toString().contains("log"))) {
            System.out.println(this.getMainHandItem().getCount());
            this.getMainHandItem().shrink(1);
            Player nearestPlayer = this.level.getNearestPlayer(this, 50);
            throwItemsTowardPos(this, this.getMainHandItem().getItem().getDefaultInstance(), Objects.requireNonNullElse(nearestPlayer, this).position());
        }


        if (hasLapis() && pickUpResourceLocation.toString().contains("log")) {
            setLapis(false);
            String sapling = pickUpResourceLocation.toString().replace("log", "sapling");
            for (Item item : Registry.ITEM) {
                if (Registry.ITEM.getKey(item).toString().equals(sapling)) {
                    Player nearestPlayer = this.level.getNearestPlayer(this, 50);
                    throwItemsTowardPos(this, item.getDefaultInstance(), Objects.requireNonNullElse(nearestPlayer, this).position());
                    this.getMainHandItem().shrink(1);
                }
            }
        }
        super.tick();
    }

    private static void throwItemsTowardPos(BlackForestSpirit spirit, ItemStack items, Vec3 vec3) {
        spirit.swing(InteractionHand.OFF_HAND);
        BehaviorUtils.throwItem(spirit, items, vec3.add(0.0D, 1.0D, 0.0D));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(HAS_LAPIS, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level, tag);
    }

    public boolean hasLapis() {
        return entityData.get(HAS_LAPIS);
    }

    public void setLapis(boolean lapis) {
        entityData.set(HAS_LAPIS, lapis);
    }

    @Override
    public void setRemainingPersistentAngerTime(int p_34448_) {
        this.remainingPersistentAngerTime = p_34448_;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }


    @Override
    public void performRangedAttack(LivingEntity entity, float index) {
        BFSAttack attack = new BFSAttack(this.level, this);
        double d0 = entity.getEyeY() - (double) 1.1F;
        double d1 = entity.getX() - this.getX();
        double d2 = d0 - attack.getY();
        double d3 = entity.getZ() - this.getZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double) 0.2F;
        attack.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(attack);

    }

    public static class FollowItemGoal extends Goal {

        public BlackForestSpirit spirit;
        public double distance;
        public float speed;


        public FollowItemGoal(BlackForestSpirit spirit, double distance, float speed) {
            this.spirit = spirit;
            this.distance = distance;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            return spirit.isAlive();
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        public void stop() {
            this.spirit.getNavigation().stop();
            super.stop();
        }

        @Override
        public void tick() {
            super.tick();
            this.spirit.followingItem = this.spirit.level.getEntitiesOfClass(ItemEntity.class, new AABB(this.spirit.blockPosition()).inflate(distance));
            if (this.spirit.followingItem.isEmpty()) return;
            boolean flag = this.spirit.hasLapis() && !Registry.ITEM.getKey(this.spirit.followingItem.get(0).getItem().getItem()).toString().contains("log");
            boolean flag1 = !this.spirit.followingItem.get(0).getItem().is(Items.LAPIS_LAZULI) && !this.spirit.hasLapis();
            if (flag || flag1) {
                this.spirit.followingItem.remove(0);
                return;
            }
            if (this.spirit.distanceToSqr(this.spirit.followingItem.get(0)) < 0.001F) {
                this.spirit.getNavigation().stop();
            } else {
                this.spirit.getNavigation().moveTo(this.spirit.followingItem.get(0), speed);
            }
        }
    }
}
