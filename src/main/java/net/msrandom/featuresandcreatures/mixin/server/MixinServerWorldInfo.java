package net.msrandom.featuresandcreatures.mixin.server;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.INBT;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraft.world.storage.VersionData;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorldInfo.class)
public class MixinServerWorldInfo implements FnCSpawnerLevelContext {

    private long jockeySpawnCoolDown;

    @Override
    public void setJockeySpawnCoolDown(long coolDown) {
        this.jockeySpawnCoolDown = coolDown;
    }

    @Override
    public long getJockeySpawnCoolDown() {
        return this.jockeySpawnCoolDown;
    }

    @Inject(method = "parse", at = @At("RETURN"))
    private static void parseFnC(Dynamic<INBT> nbtDynamic, DataFixer p_237369_1_, int p_237369_2_, CompoundTag nbt, WorldSettings p_237369_4_, VersionData p_237369_5_, DimensionGeneratorSettings p_237369_6_, Lifecycle p_237369_7_, CallbackInfoReturnable<ServerWorldInfo> cir) {
        CompoundTag featuresAndCreatures = (CompoundTag) nbtDynamic.get("featuresAndCreatures").orElseEmptyMap().getValue();
        ((FnCSpawnerLevelContext) cir.getReturnValue()).setJockeySpawnCoolDown(featuresAndCreatures.getLong("jockeySpawnCoolDown"));
    }


    @Inject(method = "setTagData", at = @At("RETURN"))
    private void saveFnC(DynamicRegistries p_237370_1_, CompoundTag nbt, CompoundTag p_237370_3_, CallbackInfo ci) {
        CompoundTag featuresAndCreatures = new CompoundTag();
        featuresAndCreatures.putLong("jockeySpawnCoolDown", this.jockeySpawnCoolDown);
        nbt.put("featuresAndCreatures", featuresAndCreatures);
    }
}
