package retropacifist.featuresandcreatures.mixin.access;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpawnPlacements.class)
public interface SpawnPlacementsAccess {

    @Invoker("register")
    static <T extends Mob> void fnc_invokeRegister(EntityType<T> entityType, SpawnPlacements.Type spawnPlacement, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate) {
        throw new Error("Mixin did not apply!");
    }
}
