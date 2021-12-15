package net.msrandom.featuresandcreatures.mixin.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerLevel;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.SaveFormat;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.entity.spawner.JockeySpawner;
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
    private List<ISpecialSpawner> customSpawners;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addFnCSpawners(MinecraftServer p_i241885_1_, Executor p_i241885_2_, SaveFormat.LevelSave p_i241885_3_, IServerWorldInfo serverWorldInfo, RegistryKey p_i241885_5_, DimensionType p_i241885_6_, IChunkStatusListener p_i241885_7_, ChunkGenerator p_i241885_8_, boolean p_i241885_9_, long p_i241885_10_, List<ISpecialSpawner> spawners, boolean p_i241885_13_, CallbackInfo ci) {
        this.customSpawners = new ArrayList<>(this.customSpawners);
        this.customSpawners.add(new JockeySpawner((FnCSpawnerLevelContext) serverWorldInfo));
    }
}
