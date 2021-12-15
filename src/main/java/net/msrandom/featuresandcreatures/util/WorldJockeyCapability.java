package net.msrandom.featuresandcreatures.util;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldJockeyCapability implements ICapabilitySerializable<ByteNBT> {
    public static final ResourceLocation ID = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "jockey_capability");

    @CapabilityInject(WorldJockeyCapability.class)
    public static Capability<WorldJockeyCapability> capability;

    private boolean isSpawned;
    private long spawnAttemptTime;

    private Level world;

    public WorldJockeyCapability(Level world) {
        this.world = world;
    }

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

    public void markSpawnAttempt() {
        if (world != null) {
            spawnAttemptTime = world.getGameTime();
        }
    }

    public long getSpawnAttemptTime() {
        return spawnAttemptTime;
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
