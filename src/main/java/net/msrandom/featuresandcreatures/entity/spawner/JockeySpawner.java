package net.msrandom.featuresandcreatures.entity.spawner;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.entity.Jockey;
import net.msrandom.featuresandcreatures.util.FnCConfig;

import javax.annotation.Nullable;
import java.util.UUID;

public class JockeySpawner implements ISpecialSpawner {
    private static final long COOLDOWN = 100; //72000L;
    public static final int MAX_OFFSET = 10;
    private int spawnChance;

    @Override
    public int tick(ServerWorld world, boolean spawnFriendlies, boolean spawnEnemies) {
        double successChance = FnCConfig.getInstance().getJockeySpawnChance();
        if (successChance <= 0) return 0;

        Context context = ((FnCSpawnerLevelContext) world.getLevelData()).jockeyContext();

        if (context == null || context.getUuid() != null) {
            return 0;
        }

        long jockeySpawnCooldown = context.getJockeySpawnCooldown();
        if (jockeySpawnCooldown <= 0) {
            return attemptSpawnJockey(world, context, successChance);
        }

        context.setJockeySpawnCooldown(jockeySpawnCooldown - 1);
        return 0;
    }

    private int attemptSpawnJockey(ServerWorld world, Context context, double successChance) {
        int defaultSpawnChance = (int) (25 * successChance);
        if (spawnChance == 0) {
            spawnChance = defaultSpawnChance;
        }

        // Increasing chance, similarly to how the wandering trader functions
        int i = spawnChance;
        spawnChance = MathHelper.clamp(spawnChance + defaultSpawnChance, 0, 75);

        if (world.random.nextInt(100) > i) {
            context.setJockeySpawnCooldown(COOLDOWN);
            return 0;
        }

        // Extra check that is never changed, except by the config value
        if (world.random.nextInt((int) (successChance * 10)) != 0) {
            context.setJockeySpawnCooldown(COOLDOWN);
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

            Jockey jockey = FnCEntities.JOCKEY.create(world);
            if (jockey == null) {
                return 0;
            }

            jockey.moveTo(position.getX(), position.getY(), position.getZ());
            jockey.finalizeSpawn(world, world.getCurrentDifficultyAt(position), SpawnReason.NATURAL, null, null);

            if (handleMount(world, jockey) && world.addFreshEntity(jockey)) {
                context.setUuid(jockey.getUUID());
                context.setJockeySpawnCooldown(-1);
                context.setPos(jockey.blockPosition());
                spawnChance = defaultSpawnChance;
                return 1;
            }
        }
        return 0;
    }

    private static boolean handleMount(ServerWorld world, Jockey jockey) {
        final MobEntity mountEntity = Jockey.getMountEntity(world, jockey);
        if (mountEntity != null) {
            mountEntity.moveTo(jockey.position());
            jockey.startRiding(mountEntity);
            return world.addFreshEntity(mountEntity);
        }
        return false;
    }

    public static class Context {

        @Nullable
        private UUID uuid;
        private long jockeySpawnCooldown;
        @Nullable
        private BlockPos pos;

        public Context(CompoundNBT nbt) {
            this(nbt.contains("uuid") ? nbt.getUUID("uuid") : null, nbt.contains("jockeySpawnCooldown") ? nbt.getLong("jockeySpawnCooldown") : COOLDOWN, nbt.contains("pos") ? NBTUtil.readBlockPos(nbt.getCompound("pos")) : null);
        }

        public Context(@Nullable UUID uuid, long jockeySpawnCooldown, @Nullable BlockPos pos) {
            this.uuid = uuid;
            this.jockeySpawnCooldown = jockeySpawnCooldown;
            this.pos = pos;
        }

        @Nullable
        public UUID getUuid() {
            return uuid;
        }

        public void setUuid(@Nullable UUID uuid) {
            this.uuid = uuid;
        }

        public long getJockeySpawnCooldown() {
            return jockeySpawnCooldown;
        }

        public void setJockeySpawnCooldown(long jockeySpawnCooldown) {
            this.jockeySpawnCooldown = jockeySpawnCooldown;
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
            compoundNBT.putLong("jockeySpawnCoolDown", this.jockeySpawnCooldown);
            return compoundNBT;
        }
    }
}
