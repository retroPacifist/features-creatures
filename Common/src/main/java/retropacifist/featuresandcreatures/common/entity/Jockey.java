package retropacifist.featuresandcreatures.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.entity.mount.Boar;
import retropacifist.featuresandcreatures.common.entity.mount.Jackalope;
import retropacifist.featuresandcreatures.common.entity.mount.Sabertooth;
import retropacifist.featuresandcreatures.common.entity.spawner.FnCSpawnerLevelContext;
import retropacifist.featuresandcreatures.common.entity.spawner.JockeySpawner;
import retropacifist.featuresandcreatures.common.network.JockeyPosPacket;
import retropacifist.featuresandcreatures.core.FnCEntities;
import retropacifist.featuresandcreatures.core.FnCSounds;
import retropacifist.featuresandcreatures.core.FnCTriggers;
import retropacifist.featuresandcreatures.mixin.SlimeSizeInvoker;
import retropacifist.featuresandcreatures.mixin.access.TargetingConditionsAccess;
import retropacifist.featuresandcreatures.platform.ModPlatform;
import retropacifist.featuresandcreatures.util.FnCConfig;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Jockey extends PathfinderMob implements Npc, Merchant, RangedAttackMob {
    private static final String POTION_TRANSLATION_KEY = "entity." + FeaturesAndCreatures.MOD_ID + ".jockey.potion";
    private static final String ARROW_TRANSLATION_KEY = "entity." + FeaturesAndCreatures.MOD_ID + ".jockey.arrow";


    private Player tradingPlayer;
    private Player followingPlayer;
    private MerchantOffers offers;
    private BlockPos lastBlockPos = BlockPos.ZERO;
    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(Jockey.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TIMER = SynchedEntityData.defineId(Jockey.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TIME_ALIVE = SynchedEntityData.defineId(Jockey.class, EntityDataSerializers.INT);
    private static final TargetingConditions TARGETING = (TargetingConditionsAccess.create(false)).range(32.0D);


    public Jockey(EntityType<? extends Jockey> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createJockeyAttributes() {
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
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.35D));
        this.goalSelector.addGoal(4, new FollowPlayerGoal(this, 12, 0.6F));
        this.goalSelector.addGoal(4, new MountFollowPlayerGoal(this, 12, 1.2F));
        this.goalSelector.addGoal(4, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
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

    @Override
    public boolean isClientSide() {
        return false;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!(itemstack.getItem() instanceof SpawnEggItem) && this.isAlive() && tradingPlayer == null) {
            if (!this.getOffers().isEmpty()) {
                if (!this.level.isClientSide) {
                    this.setTradingPlayer(player);
                    this.openTradingScreen(player, this.getDisplayName(), 1);
                }
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return size.height * 0.85F;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return tradingPlayer;
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
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
                List<MobEffectInstance> effects = new ArrayList<>();
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

                Predicate<MobEffect> blacklisted = FnCConfig.getInstance().getJockeyEffectBlacklist()::contains;

                Set<MobEffect> effectsSet = Registry.MOB_EFFECT.stream()
                        .filter(blacklisted.negate())
                        .collect(Collectors.toSet());

                for (int j = 0; j < effectCount; ++j) {
                    MobEffect effect = getRandomElement(random, effectsSet);
                    if (effect == null) effect = MobEffects.REGENERATION;
                    effectsSet.remove(effect);
                    effects.add(new MobEffectInstance(effect, 1800, generatePotionStrength(effectCount)));
                }

                Item item;
                String translationKey;
                switch (type) {
                    case DRINK -> {
                        item = Items.POTION;
                        translationKey = POTION_TRANSLATION_KEY;
                        break;
                    }
                    case SPLASH -> {
                        item = Items.SPLASH_POTION;
                        translationKey = POTION_TRANSLATION_KEY;
                        break;
                    }
                    case LINGERING -> {
                        item = Items.LINGERING_POTION;
                        translationKey = POTION_TRANSLATION_KEY;
                        break;
                    }
                    default -> {
                        item = Items.TIPPED_ARROW;
                        translationKey = ARROW_TRANSLATION_KEY;
                    }
                }

                offers.add(new MerchantOffer(new ItemStack(Items.DIAMOND, price), ItemStack.EMPTY, PotionUtils.setCustomEffects(new ItemStack(item, amount), effects).setHoverName(new TranslatableComponent(translationKey)), Integer.MAX_VALUE, 0, 1));
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
        if (this.tradingPlayer instanceof ServerPlayer) {
            FnCTriggers.JOCKEY_TRADE.trigger((ServerPlayer) this.tradingPlayer, this, offer.getResult());
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack p_110297_1_) {
    }

    @Override
    public Level getLevel() {
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
        if (isAttacking()) {
            this.setAttackTimer(this.getAttackTimer() - 1);
            if (getAttackTimer() <= 0) {
                setAttacking(false);
                setAttackTimer(10);
            }
        }
        if (isRiding(this)) {
            if (this.getVehicle() instanceof Mob)
                ((Mob) this.getVehicle()).setTarget(this.getTarget());
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return FnCSounds.JOCKEY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FnCSounds.JOCKEY_DEATH.get();
    }

    private void trackedGlobalJockey() {
        JockeySpawner.Context context = ((FnCSpawnerLevelContext) this.level.getLevelData()).jockeyContext();
        if (context != null) {
            if (!this.lastBlockPos.equals(this.blockPosition())) {
                final UUID uuid = context.getUuid();
                if (uuid != null && uuid.equals(this.uuid)) {
                    context.setPos(this.blockPosition());
                    ModPlatform.INSTANCE.sendToAllClients(((ServerLevel) this.level).players(), new JockeyPosPacket(this.blockPosition()));
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
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("TimeAlive", getTimeAlive());
        nbt.putBoolean("Attacking", isAttacking());
        nbt.putInt("AttackTimer", getAttackTimer());

    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setTimeAlive(nbt.getInt("TimeAlive"));
        setAttacking(nbt.getBoolean("Attacking"));
        setAttackTimer(nbt.getInt("AttackTimer"));
    }


    @Override
    public void rideTick() {
        super.rideTick();
        if (this.getVehicle() instanceof PathfinderMob) {
            PathfinderMob creatureentity = (PathfinderMob) this.getVehicle();
            this.yBodyRot = creatureentity.yBodyRot;
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float v) {
        this.setAttacking(true);
        Vec3 vector3d = target.getDeltaMovement();
        double d0 = target.getX() + vector3d.x - this.getX();
        double d1 = target.getEyeY() - (double) 1.1F - this.getY();
        double d2 = target.getZ() + vector3d.z - this.getZ();
        float f = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        Potion potion = Potions.HARMING;
        ThrownPotion potionentity = new ThrownPotion(this.level, this);
        potionentity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
        potionentity.setXRot(potionentity.getXRot() - 20.0F);
        potionentity.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), FnCSounds.JOCKEY_ATTACK.get(), this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
        }
        this.level.addFreshEntity(potionentity);
    }

    @Nullable
    public static Mob getMountEntity(Level world, Jockey jockey) {
        if (jockey.getY() < 30) {
            return EntityType.CAVE_SPIDER.create(world);
        }

        Biome.BiomeCategory biome = Biome.getBiomeCategory(world.getBiome(jockey.blockPosition())); // Use Biome Tags
        switch (biome) {
            case ICY -> {
                Sabertooth sabertooth = FnCEntities.SABERTOOTH.get().create(world);
                if (sabertooth != null) sabertooth.setSaddled(true);
                return sabertooth;
            }
            case SWAMP -> {
                Slime slime = EntityType.SLIME.create(world);
                if (slime != null) ((SlimeSizeInvoker) slime).callSetSize(2, true);
                return slime;
            }
            case EXTREME_HILLS -> {
                Jackalope jackalope = FnCEntities.JACKALOPE.get().create(world);
                if (jackalope != null) jackalope.setSaddled(true);
                return jackalope;
            }
            case PLAINS -> {
                Horse horse = EntityType.HORSE.create(world);
                if (horse != null) {
                    horse.equipSaddle(SoundSource.NEUTRAL);
                    horse.setBaby(true);
                }
                return horse;
            }
            default -> {
                Boar boar = FnCEntities.BOAR.get().create(world);
                if (boar != null) boar.setSaddled(true);
                return boar;
            }
        }
    }

    public enum TradeType {
        DRINK,
        SPLASH,
        LINGERING,
        ARROWS_16,
        ARROWS_32
    }

    public static class FollowPlayerGoal extends Goal {

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

    public static class MountFollowPlayerGoal extends Goal {

        public Jockey jockey;
        public double distance;
        public float speed;
        public Mob mount;


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
            mount = (Mob) jockey.getVehicle();
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
}