package retropacifist.featuresandcreatures.mixin;

import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import retropacifist.featuresandcreatures.common.entity.spawner.FnCSpawnerLevelContext;

@Mixin(LevelData.class)
public interface MixinIWorldInfo extends FnCSpawnerLevelContext {
}
