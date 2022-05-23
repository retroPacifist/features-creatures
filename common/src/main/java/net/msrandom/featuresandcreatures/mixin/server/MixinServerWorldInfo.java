package net.msrandom.featuresandcreatures.mixin.server;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.msrandom.featuresandcreatures.common.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.common.entity.spawner.JockeySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(PrimaryLevelData.class)
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

    @Inject(method = "parse", at = @At("RETURN"))
    private static void parseFnC(Dynamic<Tag> nbtDynamic, DataFixer p_237369_1_, int p_237369_2_, CompoundTag nbt, LevelSettings p_237369_4_, LevelVersion p_237369_5_, WorldGenSettings p_237369_6_, Lifecycle p_237369_7_, CallbackInfoReturnable<PrimaryLevelData> cir) {
        CompoundTag featuresAndCreatures = (CompoundTag) nbtDynamic.get("featuresAndCreatures").result().map(Dynamic::getValue).orElseGet(CompoundTag::new);
        ((FnCSpawnerLevelContext) cir.getReturnValue()).setJockeyContext(new JockeySpawner.Context(featuresAndCreatures.getCompound("jockeyContext")));
    }


    @Inject(method = "setTagData", at = @At("RETURN"))
    private void saveFnC(RegistryAccess p_237370_1_, CompoundTag nbt, CompoundTag p_237370_3_, CallbackInfo ci) {
        CompoundTag featuresAndCreatures = new CompoundTag();
        featuresAndCreatures.put("jockeyContext", this.jockeyContext.toNBT());
        nbt.put("featuresAndCreatures", featuresAndCreatures);
    }
}
