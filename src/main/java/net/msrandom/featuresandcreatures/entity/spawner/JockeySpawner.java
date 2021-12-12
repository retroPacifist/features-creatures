package net.msrandom.featuresandcreatures.entity.spawner;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.entity.Jockey;

public class JockeySpawner implements ISpecialSpawner {

    private final FnCSpawnerLevelContext fnCSpawnerLevelContext;
    private long jockeySpawnCoolDown;

    public JockeySpawner(FnCSpawnerLevelContext fnCSpawnerLevelContext) {
        this.fnCSpawnerLevelContext = fnCSpawnerLevelContext;
        this.jockeySpawnCoolDown = fnCSpawnerLevelContext.getJockeySpawnCoolDown() == -1 ? 72000L : fnCSpawnerLevelContext.getJockeySpawnCoolDown();
    }

    @Override
    public int tick(ServerWorld world, boolean spawnFriendlies, boolean spawnEnemies) {
        if (world.dimension() != World.OVERWORLD) {
            return 0;
        }
        if (jockeySpawnCoolDown <= 0) {
            ServerPlayerEntity player = world.getRandomPlayer();
            if (player != null) {
                BlockPos position = player.blockPosition().offset(world.random.nextInt(51) - 25, world.random.nextInt(51) - 25, world.random.nextInt(51) - 25);

                // Prevent suffocating
                for (BlockPos blockPos : BlockPos.betweenClosed(position.offset(-2, 0, -2), position.offset(2, 3, 2))) {
                    if (!world.isEmptyBlock(blockPos)) {
                        return 0;
                    }
                }

                // Floor finder
                for (BlockPos blockPos : BlockPos.betweenClosed(position.offset(-1, -1, -1), position.offset(1, -1, 1))) {
                    if (world.isEmptyBlock(blockPos)) {
                        return 0;
                    }
                }

                Jockey jockey = FnCEntities.JOCKEY.get().create(world);
                if (jockey == null) {
                    return 0;
                }

                jockey.moveTo(position.getX(), position.getY(), position.getZ());
                jockey.finalizeSpawn(world, world.getCurrentDifficultyAt(position), SpawnReason.NATURAL, null, null);

                mount(world, position, jockey);


                world.addFreshEntity(jockey);
                fnCSpawnerLevelContext.setJockeySpawnCoolDown(72000L);
                this.jockeySpawnCoolDown = fnCSpawnerLevelContext.getJockeySpawnCoolDown();
                return 1;
            }
        } else {
            fnCSpawnerLevelContext.setJockeySpawnCoolDown(this.jockeySpawnCoolDown--);
        }
        return 0;
    }

    private static void mount(ServerWorld world, BlockPos position, Jockey jockey) {
        if (jockey.blockPosition().getY() < 30) {
            CaveSpiderEntity caveSpiderEntity = EntityType.CAVE_SPIDER.create(world);
            caveSpiderEntity.moveTo(jockey.getX(), jockey.getY(), jockey.getZ(), jockey.yRot, 0.0F);
            caveSpiderEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(position), SpawnReason.NATURAL, null, null);
            jockey.startRiding(caveSpiderEntity);
        } else {
            final LivingEntity mountEntity = Jockey.getMountEntity(world, jockey.blockPosition());
            mountEntity.moveTo(jockey.position());
            jockey.startRiding(mountEntity);
            world.addFreshEntity(mountEntity);
        }
    }
}
