package net.msrandom.featuresandcreatures.mixin;

import net.minecraft.world.level.storage.LevelData;
import net.msrandom.featuresandcreatures.common.entity.spawner.FnCSpawnerLevelContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelData.class)
public interface MixinIWorldInfo extends FnCSpawnerLevelContext {
}
