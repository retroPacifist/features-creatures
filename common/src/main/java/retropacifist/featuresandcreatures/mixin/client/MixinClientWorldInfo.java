package retropacifist.featuresandcreatures.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import retropacifist.featuresandcreatures.common.entity.spawner.FnCSpawnerLevelContext;
import retropacifist.featuresandcreatures.common.entity.spawner.JockeySpawner;

@Mixin(ClientLevel.ClientLevelData.class)
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
