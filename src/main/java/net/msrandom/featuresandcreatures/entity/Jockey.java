package net.msrandom.featuresandcreatures.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import net.msrandom.featuresandcreatures.core.FnCTriggers;
import net.msrandom.featuresandcreatures.entity.mount.AbstractAngryMountEntity;
import net.msrandom.featuresandcreatures.entity.mount.Boar;
import net.msrandom.featuresandcreatures.entity.mount.Sabertooth;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.entity.spawner.JockeySpawner;
import net.msrandom.featuresandcreatures.mixin.SlimeSizeInvoker;
import net.msrandom.featuresandcreatures.network.JockeyPosPacket;
import net.msrandom.featuresandcreatures.network.NetworkHandler;
import net.msrandom.featuresandcreatures.util.FnCConfig;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createEntity;

public class Jockey extends CreatureEntity implements INPC, IMerchant, IAnimatable, IRangedAttackMob {
    private static final String POTION_TRANSLATION_KEY = "entity." + FeaturesAndCreatures.MOD_ID + ".jockey.potion";
    private static final String ARROW_TRANSLATION_KEY = "entity." + FeaturesAndCreatures.MOD_ID + ".jockey.arrow";

    private final AnimationFactory factory = new AnimationFactory(this);

    private PlayerEntity tradingPlayer;
    private PlayerEntity followingPlayer;
    private MerchantOffers offers;
    private BlockPos lastBlockPos = BlockPos.ZERO;
    private static final DataParameter<Boolean> ATTACKING = EntityDataManager.defineId(Jockey.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ATTACK_TIMER = EntityDataManager.defineId(Jockey.class, DataSerializers.INT);
    private static final DataParameter<Integer> TIME_ALIVE = EntityDataManager.defineId(Jockey.class, DataSerializers.INT);
    private static final EntityPredicate TARGETING = (new EntityPredicate()).range(32.0D).allowInvulnerable().allowSameTeam().allowNonAttackable().allowUnseeable();



    public Jockey(EntityType<? extends Jockey> p_i48575_1_, World p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
    }

    public static AttributeModifierMap.MutableAttribute createJockeyAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 12.0);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACKING, false);
        this.entityData.define(ATTACK_TIMER, 10);
        this.entityData.define(TIME_ALIVE, 0);
        super.defineSynchedData();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 60, 10.0F));
        this.goalSelector.addGoal(2, new SwimGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 0.35D));
        this.goalSelector.addGoal(4, new FollowPlayerGoal(this,  12, 0.6F));
        this.goalSelector.addGoal(4, new MountFollowPlayerGoal(this,  12, 1.2F));
        this.goalSelector.addGoal(4, new LookAtWithoutMovingGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35D));
        this.goalSelector.addGoal(0, new UseItemGoal<>(this, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING), SoundEvents.GENERIC_DRINK, entity -> this.getHealth() <= 6));
        this.targetSelector.addGoal(7, new HurtByTargetGoal(this));
    }

    private static <T> T getRandomElement(Random random, Collection<T> collection) {
        int size = random.nextInt(collection.size());
        int i = 0;
        for (T t : collection) {
            if (i++ == size) {
                return t;
            }
        }
        return null;
    }

    public static boolean isRiding(Jockey jockey) {
        Entity entity = jockey.getVehicle();
        return entity instanceof LivingEntity;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return null;
    }

    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!(itemstack.getItem() instanceof SpawnEggItem) && this.isAlive() && tradingPlayer == null) {
            if (!this.getOffers().isEmpty()) {
                if (!this.level.isClientSide) {
                    this.setTradingPlayer(player);
                    this.openTradingScreen(player, this.getDisplayName(), 1);
                }
            }
            return ActionResultType.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntitySize size) {
        return size.height * 0.85F;
    }

    @Nullable
    @Override
    public PlayerEntity getTradingPlayer() {
        return tradingPlayer;
    }

    @Override
    public void setTradingPlayer(@Nullable PlayerEntity player) {
        this.tradingPlayer = player;
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attack) {
        this.entityData.set(ATTACKING, attack);
    }

    public int getAttackTimer() {
        return this.entityData.get(ATTACK_TIMER);

    }

    public void setAttackTimer(int attackTimer) {
        this.entityData.set(ATTACK_TIMER, attackTimer);
    }

    public int getTimeAlive() {
        return this.entityData.get(TIME_ALIVE);

    }

    public void setTimeAlive(int time) {
        this.entityData.set(TIME_ALIVE, time);
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            for (int i = 0; i < 7; ++i) {
                List<EffectInstance> effects = new ArrayList<>();
                int price = random.nextInt(8) + 5;
                int effectCount = generateEffectCount();
                TradeType type = generateTradeType();

                int amount;
                switch (type) {
                    case ARROWS_16:
                        amount = 16;
                        break;
                    case ARROWS_32:
                        amount = 32;
                        break;
                    default:
                        amount = 1;
                }

                Predicate<Effect> blacklisted = FnCConfig.getInstance().getJockeyEffectBlacklist()::contains;

                Set<Effect> effectsSet = ForgeRegistries.POTIONS.getValues()
                        .stream()
                        .filter(blacklisted.negate())
                        .collect(Collectors.toSet());

                for (int j = 0; j < effectCount; ++j) {
                    Effect effect = getRandomElement(random, effectsSet);
                    if (effect == null) effect = Effects.REGENERATION;
                    effectsSet.remove(effect);
                    effects.add(new EffectInstance(effect, 1800, generatePotionStrength(effectCount)));
                }

                Item item;
                String translationKey;
                switch (type) {
                    case DRINK: {
                        item = Items.POTION;
                        translationKey = POTION_TRANSLATION_KEY;
                        break;
                    }
                    case SPLASH: {
                        item = Items.SPLASH_POTION;
                        translationKey = POTION_TRANSLATION_KEY;
                        break;
                    }
                    case LINGERING: {
                        item = Items.LINGERING_POTION;
                        translationKey = POTION_TRANSLATION_KEY;
                        break;
                    }
                    default: {
                        item = Items.TIPPED_ARROW;
                        translationKey = ARROW_TRANSLATION_KEY;
                    }
                }

                offers.add(new MerchantOffer(new ItemStack(Items.DIAMOND, price), ItemStack.EMPTY, PotionUtils.setCustomEffects(new ItemStack(item, amount), effects).setHoverName(new TranslationTextComponent(translationKey)), Integer.MAX_VALUE, 0, 1));
            }
        }
        return offers;
    }

    private TradeType generateTradeType() {
        TradeType type;
        int typeChance = random.nextInt(20);
        if (typeChance < 2) {
            type = TradeType.ARROWS_32;
        } else if (typeChance < 5) {
            type = TradeType.ARROWS_16;
        } else if (typeChance < 8) {
            type = TradeType.LINGERING;
        } else if (typeChance < 12) {
            type = TradeType.SPLASH;
        } else {
            type = TradeType.DRINK;
        }
        return type;
    }

    private int generateEffectCount() {
        int effectCount;
        int effectCountChance = random.nextInt(20);
        if (effectCountChance < 3) {
            effectCount = 3;
        } else if (effectCountChance < 8) {
            effectCount = 2;
        } else {
            effectCount = 1;
        }
        return effectCount;
    }

    // TODO clean this function
    private int generatePotionStrength(int effectCount) {
        int strength;
        int strengthChance = random.nextInt(100);
        switch (effectCount) {
            case 1: {
                if (strengthChance < 10) {
                    strength = 4;
                } else if (strengthChance < 35) {
                    strength = 3;
                } else if (strengthChance < 65) {
                    strength = 2;
                } else if (strengthChance < 90) {
                    strength = 1;
                } else {
                    strength = 0;
                }
                break;
            }
            case 2: {
                if (strengthChance == 0) {
                    strength = 4;
                } else if (strengthChance < 8) {
                    strength = 3;
                } else if (strengthChance < 38) {
                    strength = 2;
                } else if (strengthChance < 80) {
                    strength = 1;
                } else {
                    strength = 0;
                }
                break;
            }
            default: {
                if (strengthChance < 17) {
                    strength = 2;
                } else if (strengthChance < 40) {
                    strength = 1;
                } else {
                    strength = 0;
                }
            }
        }
        return strength;
    }

    @Override
    public void overrideOffers(@Nullable MerchantOffers offers) {
    }

    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        if (this.tradingPlayer instanceof ServerPlayerEntity) {
            FnCTriggers.JOCKEY_TRADE.trigger((ServerPlayerEntity) this.tradingPlayer, this, offer.getResult());
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack p_110297_1_) {
    }

    @Override
    public World getLevel() {
        return level;
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int xp) {
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            trackedGlobalJockey();
        }
        setTimeAlive(getTimeAlive() + 1);
        if (isAttacking()){
            this.setAttackTimer(this.getAttackTimer() - 1);
            if (getAttackTimer() <= 0){
                setAttacking(false);
                setAttackTimer(10);
            }
        }
        if (this.getVehicle() instanceof MobEntity)
        {
            MobEntity mount = ((MobEntity) this.getVehicle());

            if(getTarget() != null)
            {
                mount.setTarget(this.getTarget());
                if(mount instanceof IAngerable)
                    ((IAngerable) mount).startPersistentAngerTimer();
            }
            else if(mount.getTarget() != null)
                setTarget(mount.getTarget());
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return FnCSounds.JOCKEY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FnCSounds.JOCKEY_DEATH;
    }

    private void trackedGlobalJockey() {
        JockeySpawner.Context context = ((FnCSpawnerLevelContext) this.level.getLevelData()).jockeyContext();
        if (context != null) {
            if (!this.lastBlockPos.equals(this.blockPosition())) {
                final UUID uuid = context.getUuid();
                if (uuid != null && uuid.equals(this.uuid)) {
                    context.setPos(this.blockPosition());
                    NetworkHandler.sendToAllClients(((ServerWorld) this.level).players(), new JockeyPosPacket(this.blockPosition()));
                    this.lastBlockPos = this.blockPosition();
                }
            }
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        final FnCSpawnerLevelContext levelData = (FnCSpawnerLevelContext) this.level.getLevelData();
        final JockeySpawner.Context context = levelData.jockeyContext();
        if (context != null) {
            final UUID jockeyUUID = context.getUuid();
            if (jockeyUUID != null && jockeyUUID.equals(this.uuid)) {
                context.setUuid(null);
                context.setPos(null);
            }
        }
        super.die(damageSource);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return getTimeAlive() >= 48000 && (FnCConfig.getInstance().namedJockeyDespawn() || !hasCustomName());
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("TimeAlive", getTimeAlive());
        nbt.putBoolean("Attacking", isAttacking());
        nbt.putInt("AttackTimer", getAttackTimer());

    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        setTimeAlive(nbt.getInt("TimeAlive"));
        setAttacking(nbt.getBoolean("Attacking"));
        setAttackTimer(nbt.getInt("AttackTimer"));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (this.isOnGround() && event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jockey.walk", true));
            return PlayState.CONTINUE;
        } else if (this.isAttacking()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jockey.potion", true));
            return PlayState.CONTINUE;
        }else if (this.isHolding(Items.POTION)) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jockey.drink", true));
            return PlayState.CONTINUE;
        } if (isRiding(this)) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jockey.sit", true));
            return PlayState.CONTINUE;
        } else{
            return PlayState.STOP;
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public void rideTick() {
        super.rideTick();
        if (this.getVehicle() instanceof CreatureEntity) {
            CreatureEntity creatureentity = (CreatureEntity) this.getVehicle();
            this.yBodyRot = creatureentity.yBodyRot;
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float v) {
        this.setAttacking(true);
        Vector3d vector3d = target.getDeltaMovement();
        double d0 = target.getX() + vector3d.x - this.getX();
        double d1 = target.getEyeY() - (double) 1.1F - this.getY();
        double d2 = target.getZ() + vector3d.z - this.getZ();
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2);
        Potion potion = Potions.HARMING;
        PotionEntity potionentity = new PotionEntity(this.level, this);
        potionentity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
        potionentity.xRot -= -20.0F;
        potionentity.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), FnCSounds.JOCKEY_ATTACK, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
        }
        this.level.addFreshEntity(potionentity);
    }

    @Nullable
    public static MobEntity getMountEntity(World world, Jockey jockey) {
        if (jockey.getY() < 30) {
            return EntityType.CAVE_SPIDER.create(world);
        }

        Biome.Category biome = world.getBiome(jockey.blockPosition()).getBiomeCategory();
        switch (biome) {
            case ICY:
                Sabertooth sabertooth = FnCEntities.SABERTOOTH.create(world);
                if (sabertooth != null) sabertooth.setSaddled(true);
                return sabertooth;
            case SWAMP:
                SlimeEntity slime = EntityType.SLIME.create(world);
                if (slime != null) ((SlimeSizeInvoker) slime).callSetSize(2, true);
                return slime;
            case EXTREME_HILLS:
                Jackalope jackalope = FnCEntities.JACKALOPE.create(world);
                if (jackalope != null) jackalope.setSaddled(true);
                return jackalope;
            case PLAINS:
                HorseEntity horse = EntityType.HORSE.create(world);
                if (horse != null) {
                    horse.equipSaddle(SoundCategory.NEUTRAL);
                    horse.setBaby(true);
                }
                return horse;
            default:
                return createEntity(FnCEntities.BOAR, world, boarEntity -> boarEntity.setSaddled(true));
        }
    }

    public enum TradeType {
        DRINK,
        SPLASH,
        LINGERING,
        ARROWS_16,
        ARROWS_32
    }

    public static class FollowPlayerGoal extends Goal{

        public Jockey jockey;
        public double distance;
        public float speed;


        public FollowPlayerGoal(Jockey jockey, double distance, float speed) {
            this.jockey = jockey;
            this.distance = distance;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            return jockey.isAlive();
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        public void stop() {
            this.jockey.getNavigation().stop();
            super.stop();
        }

        @Override
        public void tick() {
            super.tick();
            this.jockey.followingPlayer = this.jockey.level.getNearestPlayer(TARGETING, this.jockey);
            if (this.jockey.followingPlayer == null) return;
            if (this.jockey.distanceToSqr(this.jockey.followingPlayer) < distance) {
                this.jockey.getNavigation().stop();
            } else {
                this.jockey.getNavigation().moveTo(this.jockey.followingPlayer, speed);
            }
        }
    }

    public static class MountFollowPlayerGoal extends Goal{

        public Jockey jockey;
        public double distance;
        public float speed;
        public MobEntity mount;


        public MountFollowPlayerGoal(Jockey jockey, double distance, float speed) {
            this.jockey = jockey;
            this.distance = distance;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            return isRiding(jockey);
        }

        @Override
        public void start() {
            mount = (MobEntity) jockey.getVehicle();
            super.start();
        }

        @Override
        public void stop() {
            this.jockey.getNavigation().stop();
            super.stop();
        }

        @Override
        public void tick() {
            super.tick();
            this.jockey.followingPlayer = this.jockey.level.getNearestPlayer(TARGETING, this.jockey);
            if (this.jockey.followingPlayer == null) return;
            if (this.mount.distanceToSqr(this.jockey.followingPlayer) < distance) {
                this.mount.getNavigation().stop();
            } else {
                this.mount.getNavigation().moveTo(this.jockey.followingPlayer, speed);
            }
        }
    }

    @Override
    public boolean canBeLeashed(PlayerEntity player) {
        return false;
    }
}