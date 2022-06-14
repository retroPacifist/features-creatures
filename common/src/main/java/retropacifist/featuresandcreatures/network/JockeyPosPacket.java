package retropacifist.featuresandcreatures.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import retropacifist.featuresandcreatures.common.entity.spawner.FnCSpawnerLevelContext;

import javax.annotation.Nullable;

public class JockeyPosPacket implements FnCPacket {

    private final BlockPos jockeyPos;

    public JockeyPosPacket(BlockPos jockeyPos) {
        this.jockeyPos = jockeyPos;
    }

    public static JockeyPosPacket readFromPacket(FriendlyByteBuf buf) {
        return new JockeyPosPacket(buf.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.jockeyPos);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (level != null) {
            ((FnCSpawnerLevelContext) level.getLevelData()).jockeyContext().setPos(this.jockeyPos);
        } else {
            throw new UnsupportedOperationException("Client level is not constructed!");
        }

    }
}