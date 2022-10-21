package net.msrandom.featuresandcreatures.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Random;

public class Tbh extends PathfinderMob implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    protected static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(Tbh.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<String> EYE_CONTACT_PLAYER = SynchedEntityData.defineId(Tbh.class, EntityDataSerializers.STRING);

    private LookAtPlayerGoalTbh lookAtPlayerGoal;

    public Tbh(EntityType<? extends Tbh> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public static boolean checkSpawnRules(EntityType<Tbh> type, LevelAccessor world, MobSpawnType spawnType, BlockPos pos, Random random) {
        if (world.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)) {
            Holder<Biome> biome = world.getBiome(pos);
            if (biome.is(Biomes.PLAINS) || biome.is(Biomes.SUNFLOWER_PLAINS) || biome.is(Biomes.MEADOW) || biome.is(Biomes.FLOWER_FOREST)) {
                return true;
            }
            return checkMobSpawnRules(type, world, spawnType, pos, random);
        }
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RUNNING, false);
        this.entityData.define(EYE_CONTACT_PLAYER, "");
    }

    @Override
    protected void registerGoals() {
        this.lookAtPlayerGoal = new LookAtPlayerGoalTbh(this, 16.0F, 0.8F);
        this.goalSelector.addGoal(3, lookAtPlayerGoal);
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoalTbh(this, this.lookAtPlayerGoal, 1.0D, 0.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2D) {
            @Override
            public void start() {
                super.start();
                if (this.mob instanceof Tbh tbh) {
                    tbh.setRunning(true);
                }
            }
            @Override
            public void stop() {
                super.stop();
                if (this.mob instanceof Tbh tbh) {
                    tbh.setRunning(false);
                }
            }
        });
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 12.0).add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.FOLLOW_RANGE, 48.0D);
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
        if (this.isOnGround() && !event.isMoving()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.tbh.idle", true));
            return PlayState.CONTINUE;
        }
        if (this.isOnGround() && event.isMoving() && !this.isRunning()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.tbh.walk", true));
            return PlayState.CONTINUE;
        }
        if (this.isRunning()) {
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.tbh.run", true));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    public void setRunning(boolean running) {
        this.entityData.set(RUNNING, running);
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setLookingAtPlayer(String playerUUID) {
        this.entityData.set(EYE_CONTACT_PLAYER, playerUUID);
    }

    public String getLookingAtPlayer() {
        return this.entityData.get(EYE_CONTACT_PLAYER);
    }

    @SuppressWarnings("resource")
    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            Player player = Minecraft.getInstance().player;
            if (this.getLookingAtPlayer().equals(player.getStringUUID())) {
                System.out.println("o_o");
            } else {
                System.out.println("-_-");
            }
        } else {
            Entity ent = this.lookAtPlayerGoal.getLookAt();
            // not sure if updating a synched variable to the same value uses the network, so err on side of caution
            if (this.lookAtPlayerGoal.isReciprocated && ent instanceof Player player) {
                String uuid = player.getStringUUID();
                if (!this.getLookingAtPlayer().equals(uuid)) {
                    this.setLookingAtPlayer(uuid);
                }
            } else if (!this.getLookingAtPlayer().isEmpty()) {
                this.setLookingAtPlayer("");
            }
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getItemInHand(player.getUsedItemHand()).isEmpty()) { //player.getItemInHand(hand).isEmpty() causes false positives?
            player.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.25, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    //TODO NOT FINISHED
    @Override
    protected void dropExperience() {
        if (this.shouldDropExperience() && this.level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)) {
            Vec3 pos = this.position();
            this.level.addFreshEntity(new ExperienceOrb(this.level, pos.x, pos.y, pos.z, -ExperienceOrb.getExperienceValue(this.random.nextInt(1, 3))) {
                @Override
                public void playerTouch(Player player) {
                    if (level.isClientSide()) return;
                    player.giveExperiencePoints(this.value); // otherwise it doesn't count
                    super.playerTouch(player);
                }
            });
        }
    }

    private class LookAtPlayerGoalTbh extends LookAtPlayerGoal {

        private boolean isReciprocated = false;

        public LookAtPlayerGoalTbh(Tbh arg, float f, float g) {
            super(arg, Player.class, f, g);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.lookAt != null && (this.isReciprocated || this.mob.distanceTo(this.lookAt) < this.lookDistance)) {
                return true;
            }
            return super.canContinueToUse();
        }

        @Override
        public void tick() {
            super.tick();
            this.isReciprocated = this.lookAt != null && this.lookAt.getViewVector(1.0f).normalize().dot(this.mob.getViewVector(1.0f).normalize()) < -0.975;
        }

        public Entity getLookAt() {
            return this.lookAt;
        }
    }

    private class WaterAvoidingRandomStrollGoalTbh extends WaterAvoidingRandomStrollGoal {

        private LookAtPlayerGoalTbh lookGoal;

        public WaterAvoidingRandomStrollGoalTbh(Tbh arg, LookAtPlayerGoalTbh goal, double d, float f) {
            super(arg, d, f);
            this.lookGoal = goal;
        }

        @Override
        public boolean canUse() {
            if (this.lookGoal.getLookAt() != null) return false;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.lookGoal.getLookAt() != null) return false;
            return super.canContinueToUse();
        }
    }
}
