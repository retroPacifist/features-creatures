package net.msrandom.featuresandcreatures.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.msrandom.featuresandcreatures.common.entity.spawner.FnCSpawnerLevelContext;

import java.util.function.Supplier;

public class JockeyPosPacket {

    private final BlockPos jockeyPos;

    public JockeyPosPacket(BlockPos jockeyPos) {
        this.jockeyPos = jockeyPos;
    }

    public static void writeToPacket(JockeyPosPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.jockeyPos);
    }

    public static JockeyPosPacket readFromPacket(FriendlyByteBuf buf) {
        return new JockeyPosPacket(buf.readBlockPos());
    }

    public static void handle(JockeyPosPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                Minecraft minecraft = Minecraft.getInstance();
                ClientLevel world = minecraft.level;
                if (world != null) {
                    ((FnCSpawnerLevelContext) world.getLevelData()).jockeyContext().setPos(message.jockeyPos);
                } else {
                    throw new UnsupportedOperationException("Client level is not constructed!");
                }
            });
        }
    }
}