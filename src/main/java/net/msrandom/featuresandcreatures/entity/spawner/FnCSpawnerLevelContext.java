package net.msrandom.featuresandcreatures.entity.spawner;

public interface FnCSpawnerLevelContext {

    default void setJockeySpawnCoolDown(long spawnDelay) {

    }

    default long getJockeySpawnCoolDown() {
        return 0;
    }

}
