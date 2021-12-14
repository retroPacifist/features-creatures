package net.msrandom.featuresandcreatures.mixin.client;

import net.minecraft.client.world.ClientWorld;
import net.msrandom.featuresandcreatures.entity.spawner.FnCSpawnerLevelContext;
import net.msrandom.featuresandcreatures.entity.spawner.JockeySpawner;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientWorld.ClientWorldInfo.class)
public class MixinClientWorldInfo implements FnCSpawnerLevelContext {

    private final JockeySpawner.Context context = new JockeySpawner.Context(null, -1, null);

    @Nullable
    @Override
    public JockeySpawner.Context jockeyContext() {
        return context;
    }

    @Override
    public void setJockeyContext(JockeySpawner.Context context) {
    }
}
