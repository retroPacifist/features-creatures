package net.msrandom.featuresandcreatures.entity.spawner;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.entity.Jockey;
import net.msrandom.featuresandcreatures.util.Util;

import javax.annotation.Nullable;
import java.util.UUID;

public class JockeySpawner implements ISpecialSpawner {
   public static final int MAX_OFFSET = 10;

    private final Context fnCSpawnerLevelContext;
    private long jockeySpawnCoolDown;

    public JockeySpawner(Context fnCSpawnerLevelContext) {
        this.fnCSpawnerLevelContext = fnCSpawnerLevelContext;
        this.jockeySpawnCoolDown = fnCSpawnerLevelContext.getJockeySpawnCoolDown();
    }

    @Override
    public int tick(ServerWorld world, boolean spawnFriendlies, boolean spawnEnemies) {
        if (world.dimension() != World.OVERWORLD || fnCSpawnerLevelContext.getUuid() != null) {
            return 0;
        }

        if (jockeySpawnCoolDown <= 0) {
//            if (world.random.nextDouble() < 0.5) {
//                fnCSpawnerLevelContext.setJockeySpawnCoolDown(72000L);
//                jockeySpawnCoolDown = fnCSpawnerLevelContext.getJockeySpawnCoolDown();
//                return 0;
//            }

            ServerPlayerEntity player = world.getRandomPlayer();
            if (player != null) {
                BlockPos position = player.blockPosition().offset(world.random.nextInt(MAX_OFFSET) - (MAX_OFFSET / 2), world.random.nextInt(MAX_OFFSET) - (MAX_OFFSET / 2), world.random.nextInt(MAX_OFFSET) - (MAX_OFFSET / 2));

                // Prevent suffocating
                for (BlockPos blockPos : BlockPos.betweenClosed(position.offset(-2, 0, -2), position.offset(2, 3, 2))) {
                    if (!world.isEmptyBlock(blockPos)) {
                        return 0;
                    }
                }

                // Floor finder
                for (BlockPos blockPos : BlockPos.betweenClosed(position.offset(-1, -1, -1), position.offset(1, -1, 1))) {
                    if (world.isEmptyBlock(blockPos) && !world.getFluidState(blockPos).isEmpty()) {
                        return 0;
                    }
                }

                Jockey jockey = FnCEntities.JOCKEY.get().create(world);
                if (jockey == null) {
                    return 0;
                }

                jockey.moveTo(position.getX(), position.getY(), position.getZ());
                jockey.finalizeSpawn(world, world.getCurrentDifficultyAt(position), SpawnReason.NATURAL, null, null);


                if (handleMount(world, jockey) && world.addFreshEntity(jockey)) {
                    fnCSpawnerLevelContext.setUuid(jockey.getUUID());
                    fnCSpawnerLevelContext.setJockeySpawnCoolDown(-1);
                    fnCSpawnerLevelContext.setPos(jockey.blockPosition());
                    jockeySpawnCoolDown = fnCSpawnerLevelContext.getJockeySpawnCoolDown();
                    return 1;
                }
            }
        } else {
            fnCSpawnerLevelContext.setJockeySpawnCoolDown(jockeySpawnCoolDown--);
        }
        return 0;
    }

    private static boolean handleMount(ServerWorld world, Jockey jockey) {
        final MobEntity mountEntity = Jockey.getMountEntity(world, jockey);
        mountEntity.moveTo(jockey.position());
        jockey.startRiding(mountEntity);
        return world.addFreshEntity(mountEntity);
    }

    public static class Context {

        @Nullable
        private UUID uuid;
        private long jockeySpawnCoolDown;
        @Nullable
        private BlockPos pos;

        public Context(CompoundNBT nbt) {
            this(nbt.getUUID("uuid"), nbt.getLong("jockeySpawnCoolDown"), Util.fromCompound(nbt.getCompound("pos")));
        }

        public Context(@Nullable UUID uuid, long jockeySpawnCoolDown, @Nullable BlockPos pos) {
            this.uuid = uuid;
            this.jockeySpawnCoolDown = jockeySpawnCoolDown;
            this.pos = pos;
        }

        @Nullable
        public UUID getUuid() {
            return uuid;
        }

        public void setUuid(@Nullable UUID uuid) {
            this.uuid = uuid;
        }

        public long getJockeySpawnCoolDown() {
            return jockeySpawnCoolDown;
        }

        public void setJockeySpawnCoolDown(long jockeySpawnCoolDown) {
            this.jockeySpawnCoolDown = jockeySpawnCoolDown;
        }

        @Nullable
        public BlockPos getPos() {
            return pos;
        }

        public void setPos(@Nullable BlockPos pos) {
            this.pos = pos;
        }

        public CompoundNBT toNBT() {
            CompoundNBT compoundNBT = new CompoundNBT();
            if (uuid != null) {
                compoundNBT.putUUID("uuid", this.uuid);
            }
            if (pos != null) {
                compoundNBT.put("pos", Util.posToCompound(pos));
            }
            compoundNBT.putLong("jockeySpawnCoolDown", this.jockeySpawnCoolDown);
            return compoundNBT;
        }
    }
}
