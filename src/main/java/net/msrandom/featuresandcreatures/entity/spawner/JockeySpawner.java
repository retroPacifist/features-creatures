package net.msrandom.featuresandcreatures.entity.spawner;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.entity.Jockey;

import javax.annotation.Nullable;
import java.util.UUID;

public class JockeySpawner implements ISpecialSpawner {
    public static final int MAX_OFFSET = 10;

    public JockeySpawner() {
    }

    @Override
    public int tick(ServerWorld world, boolean spawnFriendlies, boolean spawnEnemies) {
        Context context = ((FnCSpawnerLevelContext) world.getLevelData()).jockeyContext();

        if (world.dimension() != World.OVERWORLD || context.getUuid() != null) {
            return 0;
        }

        long jockeySpawnCoolDown = context.getJockeySpawnCoolDown();
        if (jockeySpawnCoolDown <= 0) {
            attemptSpawnJockey(world, context, 1.0);
        } else {
            context.setJockeySpawnCoolDown(jockeySpawnCoolDown - 1);
        }
        return 0;
    }

    private static int attemptSpawnJockey(ServerWorld world, Context context, double successChance) {
        if (world.random.nextDouble() > successChance) {
            context.setJockeySpawnCoolDown(72000L);
            return 0;
        }

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
                context.setUuid(jockey.getUUID());
                context.setJockeySpawnCoolDown(-1);
                context.setPos(jockey.blockPosition());
                return 1;
            }
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
            this(nbt.contains("uuid") ? nbt.getUUID("uuid") : null, nbt.contains("jockeySpawnCoolDown") ? nbt.getLong("jockeySpawnCoolDown") : 72000L, nbt.contains("pos") ? NBTUtil.readBlockPos(nbt.getCompound("pos")) : null);
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
                compoundNBT.put("pos", NBTUtil.writeBlockPos(pos));
            }
            compoundNBT.putLong("jockeySpawnCoolDown", this.jockeySpawnCoolDown);
            return compoundNBT;
        }

    }
}
