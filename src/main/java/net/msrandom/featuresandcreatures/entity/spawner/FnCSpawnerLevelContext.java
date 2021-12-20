package net.msrandom.featuresandcreatures.entity.spawner;

import javax.annotation.Nullable;

public interface FnCSpawnerLevelContext {


    @Nullable
    default JockeySpawner.Context jockeyContext() {
        return null;
    }

    default void setJockeyContext(JockeySpawner.Context context) {
        throw new UnsupportedOperationException();
    }
}
