package net.msrandom.featuresandcreatures.entity;

import com.mojang.math.Vector3d;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.UseItemGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCTriggers;
import net.msrandom.featuresandcreatures.mixin.SlimeSizeInvoker;
import net.msrandom.featuresandcreatures.util.FnCConfig;
import net.msrandom.featuresandcreatures.util.WorldJockeyCapability;
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

public class Jockey extends CreatureEntity implements Npc, Merchant, IAnimatable, RangedAttackMob {
    private static final String POTION_TRANSLATION_KEY = "entity." + FeaturesAndCreatures.MOD_ID + ".jockey.potion";
    private static final String ARROW_TRANSLATION_KEY = "entity." + FeaturesAndCreatures.MOD_ID + ".jockey.arrow";

    private final AnimationFactory factory = new AnimationFactory(this);
    private int timeAlive = 0;

    private Player tradingPlayer;
    private MerchantOffers offers;

    public Jockey(EntityType<? extends Jockey> p_i48575_1_, Level p_i48575_2_) {
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
        this.goalSelector.addGoal(4, new LookAtWithoutMovingGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(5, new LookAtGoal(this, Player.class, 6.0F));
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

    public ActionResultType mobInteract(Player player, InteractionHand hand) {
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
    public Player getTradingPlayer() {
        return tradingPlayer;
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
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

                Predicate<MobEffect> blacklisted = FnCConfig.getInstance().getJockeyEffectBlacklist()::contains;

                Set<MobEffect> effectsSet = ForgeRegistries.POTIONS.getValues()
                        .stream()
                        .filter(blacklisted.negate())
                        .collect(Collectors.toSet());

                for (int j = 0; j < effectCount; ++j) {
                    MobEffect effect = getRandomElement(random, effectsSet);
                    if (effect == null) effect = MobEffects.REGENERATION;
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
        ++timeAlive;
    }

    @Override
    public void remove(boolean keepData) {
        super.remove(keepData);
        level.getCapability(WorldJockeyCapability.capability).ifPresent(capability -> capability.setSpawned(false));
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return timeAlive >= 48000;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_213281_1_) {
        super.addAdditionalSaveData(p_213281_1_);
        p_213281_1_.putInt("TimeAlive", timeAlive);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_70037_1_) {
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
        float f = Mth.sqrt(d0 * d0 + d2 * d2);
        Potion potion = Potions.HARMING;
        PotionEntity potionentity = new PotionEntity(this.level, this);
        potionentity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
        potionentity.xRot -= -20.0F;
        potionentity.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);
        if (!this.isSilent()) {
            this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
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

    public static Mob getMountEntity(Level world, Jockey jockey) {
        if (jockey.getY() < 30) {
            return EntityType.CAVE_SPIDER.create(world);
        }

        Biome.BiomeCategory biome = world.getBiome(jockey.blockPosition()).getBiomeCategory();
        switch (biome) {
            case ICY:
                Sabertooth sabertooth = FnCEntities.SABERTOOTH.get().create(world);
                if (sabertooth != null) sabertooth.setSaddled(true);
                return sabertooth;
            case SWAMP:
                Slime slime = EntityType.SLIME.create(world);
                if (slime != null) ((SlimeSizeInvoker) slime).callSetSize(2, true);
                return slime;
            case EXTREME_HILLS:
                Jackalope jackalope = FnCEntities.JACKALOPE.get().create(world);
                if (jackalope != null) jackalope.setSaddled(true);
                return jackalope;
            case PLAINS:
                Horse horse = EntityType.HORSE.create(world);
                if (horse != null) {
                    horse.equipSaddle(SoundSource.NEUTRAL);
                }
                return horse;
            default:
                Boar boar = FnCEntities.BOAR.get().create(world);
                if (boar != null) boar.setSaddled(true);
                return boar;
        }
    }
}