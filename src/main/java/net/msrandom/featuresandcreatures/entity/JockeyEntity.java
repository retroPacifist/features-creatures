package net.msrandom.featuresandcreatures.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.msrandom.featuresandcreatures.WorldJockeyCapability;

import javax.annotation.Nullable;

public class JockeyEntity extends AbstractVillagerEntity {
    private int timeAlive = 0;

    public JockeyEntity(EntityType<? extends JockeyEntity> p_i48575_1_, World p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
    }

    public static AttributeModifierMap.MutableAttribute createJockeyAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 12.0);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int i = 3 + this.random.nextInt(4);
            this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }
    }

    @Override
    protected void updateTrades() {

    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
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
    public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
        super.addAdditionalSaveData(p_213281_1_);
        p_213281_1_.putInt("TimeAlive", timeAlive);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        timeAlive = p_70037_1_.getInt("TimeAlive");
    }
}
