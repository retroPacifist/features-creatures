package net.msrandom.featuresandcreatures.mixin.server;

import net.minecraft.world.storage.IServerWorldInfo;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IServerWorldInfo.class)
public interface MixinIServerWorldInfo extends FnCSpawnerLevelContext {
}
