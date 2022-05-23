package net.msrandom.featuresandcreatures.mixin.server;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.msrandom.featuresandcreatures.common.entity.spawner.JockeySpawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class MixinServerLevel {

    @Mutable
    @Shadow
    @Final
    private List<CustomSpawner> customSpawners;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addFnCSpawners(MinecraftServer p_i241885_1_, Executor p_i241885_2_, LevelStorageSource.LevelStorageAccess p_i241885_3_, ServerLevelData serverWorldInfo, ResourceKey<Level> worldRegistryKey, DimensionType p_i241885_6_, ChunkProgressListener p_i241885_7_, ChunkGenerator p_i241885_8_, boolean p_i241885_9_, long p_i241885_10_, List<CustomSpawner> spawners, boolean p_i241885_13_, CallbackInfo ci) {
        this.customSpawners = new ArrayList<>(this.customSpawners);
        if (worldRegistryKey == Level.OVERWORLD) {
            this.customSpawners.add(new JockeySpawner());
        }
    }
}
