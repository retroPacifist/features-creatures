package net.msrandom.featuresandcreatures.mixin;

import net.minecraft.world.storage.IWorldInfo;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IWorldInfo.class)
public interface MixinIWorldInfo extends FnCSpawnerLevelContext {
}
