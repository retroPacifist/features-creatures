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
import net.msrandom.featuresandcreatures.core.FnCTriggers;
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
    private int timeAlive = 0;

    private PlayerEntity tradingPlayer;
    private MerchantOffers offers;
    private BlockPos lastBLockPos = BlockPos.ZERO;

    public Jockey(EntityType<? extends Jockey> p_i48575_1_, World p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
    }

    public static AttributeModifierMap.MutableAttribute createJockeyAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 12.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 60, 10.0F));
        this.goalSelector.addGoal(2, new SwimGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 0.35D));
        this.goalSelector.addGoal(4, new LookAtWithoutMovingGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(7, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new UseItemGoal<>(this, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING), SoundEvents.GENERIC_DRINK, (entity) -> {
            return this.getHealth() <= 6;
        }));
    }

    private static <T> T getRandomElement(Random random, Collection<T> collection) {
        int size = random.nextInt(collection.size());
        int i = 0;
        for (T t : collection) {
            if (i == size) {
                return t;
            }
            i++;
        }
        return null;
    }

    public static boolean isRiding(Jockey jockey) {
        Entity entity = jockey.getVehicle();
        return entity instanceof Jackalope || entity instanceof Boar;
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
        int typeChance = random.nextInt(100);
        if (typeChance < 10) {
            type = TradeType.ARROWS_32;
        } else if (typeChance < 25) {
            type = TradeType.ARROWS_16;
        } else if (typeChance < 40) {
            type = TradeType.LINGERING;
        } else if (typeChance < 60) {
            type = TradeType.SPLASH;
        } else {
            type = TradeType.DRINK;
        }
        return type;
    }

    private int generateEffectCount() {
        int effectCount;
        int effectCountChance = random.nextInt(100);
        if (effectCountChance < 15) {
            effectCount = 3;
        } else if (effectCountChance < 40) {
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
        ++timeAlive;
    }

    private void trackedGlobalJockey() {
        JockeySpawner.Context context = ((FnCSpawnerLevelContext) this.level.getLevelData()).jockeyContext();
        if (context != null) {
            if (!this.lastBLockPos.equals(this.blockPosition())) {
                final UUID uuid = context.getUuid();
                if (uuid != null && uuid.equals(this.uuid)) {
                    context.setPos(this.blockPosition());
                    NetworkHandler.sendToAllClients(((ServerWorld) this.level).players(), new JockeyPosPacket(this.blockPosition()));
                    this.lastBLockPos = this.blockPosition();
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
    public boolean removeWhenFarAway(double p_213397_1_) {
        return timeAlive >= 48000;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
        super.addAdditionalSaveData(p_213281_1_);
        p_213281_1_.putInt("TimeAlive", timeAlive);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        timeAlive = p_70037_1_.getInt("TimeAlive");
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 0;
        if (this.isOnGround() && event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jockey.walk", true));
            return PlayState.CONTINUE;
        } else if (isRiding(this)) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.jockey.sit", true));
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
    public void performRangedAttack(LivingEntity jockey, float v) {
        Vector3d vector3d = jockey.getDeltaMovement();
        double d0 = jockey.getX() + vector3d.x - this.getX();
        double d1 = jockey.getEyeY() - (double) 1.1F - this.getY();
        double d2 = jockey.getZ() + vector3d.z - this.getZ();
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2);
        Potion potion = Potions.HARMING;
        PotionEntity potionentity = new PotionEntity(this.level, this);
        potionentity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
        potionentity.xRot -= -20.0F;
        potionentity.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);
        if (!this.isSilent()) {
            this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
        }
        this.level.addFreshEntity(potionentity);
    }

    public enum TradeType {
        DRINK,
        SPLASH,
        LINGERING,
        ARROWS_16,
        ARROWS_32
    }

    public static MobEntity getMountEntity(World world, Jockey jockey) {
        if (jockey.getY() < 30) {
            return EntityType.CAVE_SPIDER.create(world);
        }

        Biome.Category biome = world.getBiome(jockey.blockPosition()).getBiomeCategory();
        switch (biome) {
            case ICY:
                Sabertooth sabertooth = FnCEntities.SABERTOOTH.get().create(world);
                if (sabertooth != null) sabertooth.setSaddled(true);
                return sabertooth;
            case SWAMP:
                SlimeEntity slime = EntityType.SLIME.create(world);
                if (slime != null) ((SlimeSizeInvoker) slime).callSetSize(2, true);
                return slime;
            case EXTREME_HILLS:
                Jackalope jackalope = FnCEntities.JACKALOPE.get().create(world);
                if (jackalope != null) jackalope.setSaddled(true);
                return jackalope;
            case PLAINS:
                HorseEntity horse = EntityType.HORSE.create(world);
                if (horse != null) {
                    horse.equipSaddle(SoundCategory.NEUTRAL);
                }
                return horse;
            default:
                return createEntity(FnCEntities.BOAR.get(), world, boarEntity -> boarEntity.setSaddled(true));
        }
    }
}