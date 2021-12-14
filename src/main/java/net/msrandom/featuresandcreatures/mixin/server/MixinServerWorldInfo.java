package net.msrandom.featuresandcreatures.mixin.server;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraft.world.storage.VersionData;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.entity.spawner.JockeySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ServerWorldInfo.class)
public class MixinServerWorldInfo implements FnCSpawnerLevelContext {

    private JockeySpawner.Context jockeyContext = new JockeySpawner.Context(null, 0, null);

    @Override
    @Nullable
    public JockeySpawner.Context jockeyContext() {
        return jockeyContext;
    }

    @Override
    public void setJockeyContext(JockeySpawner.Context context) {
        this.jockeyContext = context;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "parse", at = @At("RETURN"))
    private static void parseFnC(Dynamic<INBT> nbtDynamic, DataFixer p_237369_1_, int p_237369_2_, CompoundNBT nbt, WorldSettings p_237369_4_, VersionData p_237369_5_, DimensionGeneratorSettings p_237369_6_, Lifecycle p_237369_7_, CallbackInfoReturnable<ServerWorldInfo> cir) {
        CompoundNBT featuresAndCreatures = (CompoundNBT) nbtDynamic.get("featuresAndCreatures").result().map(Dynamic::getValue).orElseGet(CompoundNBT::new);
        ((FnCSpawnerLevelContext) cir.getReturnValue()).setJockeyContext(new JockeySpawner.Context(featuresAndCreatures.contains("jockeyContext") ? (CompoundNBT) featuresAndCreatures.get("jockeyContext") : new CompoundNBT()));
    }


    @Inject(method = "setTagData", at = @At("RETURN"))
    private void saveFnC(DynamicRegistries p_237370_1_, CompoundNBT nbt, CompoundNBT p_237370_3_, CallbackInfo ci) {
        CompoundNBT featuresAndCreatures = new CompoundNBT();
        featuresAndCreatures.put("jockeyContext", this.jockeyContext.toNBT());
        nbt.put("featuresAndCreatures", featuresAndCreatures);
    }
}
