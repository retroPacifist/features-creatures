package net.msrandom.featuresandcreatures.util;

import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldJockeyCapability implements ICapabilitySerializable<ByteNBT> {
    public static final ResourceLocation ID = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "jockey_capability");

    @CapabilityInject(WorldJockeyCapability.class)
    public static Capability<WorldJockeyCapability> capability;

    private boolean isSpawned;

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return capability.orEmpty(cap, LazyOptional.of(() -> this));
    }

    @Override
    public ByteNBT serializeNBT() {
        return ByteNBT.valueOf(isSpawned);
    }

    @Override
    public void deserializeNBT(ByteNBT nbt) {
        isSpawned = nbt.getAsByte() == 1;
    }

    public boolean isSpawned() {
        return isSpawned;
    }

    public void setSpawned(boolean spawned) {
        this.isSpawned = spawned;
    }

    public static class Storage implements Capability.IStorage<WorldJockeyCapability> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<WorldJockeyCapability> capability, WorldJockeyCapability instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<WorldJockeyCapability> capability, WorldJockeyCapability instance, Direction side, INBT nbt) {
            instance.deserializeNBT((ByteNBT) nbt);
        }
    }
}
