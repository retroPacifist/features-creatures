package retropacifist.featuresandcreatures.network;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel SIMPLE_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(FeaturesAndCreatures.MOD_ID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int idx = 0;
        for (Map.Entry<String, FnCPacket.Handler<?>> entry : FnCPacket.S2C_PACKETS.entrySet()) {
            registerMessage(idx++, entry.getValue());
        }
    }

    public static <T extends FnCPacket> void registerMessage(int idx, FnCPacket.Handler<T> handler) {
        SIMPLE_CHANNEL.registerMessage(idx, handler.clazz(), handler.write(), handler.read(), (t, contextSupplier) -> handle(t, contextSupplier, handler.handle()));
    }

    public static <P extends FnCPacket> void sendToPlayer(ServerPlayer playerEntity, P packet) {
        SIMPLE_CHANNEL.sendTo(packet, playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <P extends FnCPacket> void sendToAllPlayers(List<ServerPlayer> playerEntities, P packet) {
        for (ServerPlayer playerEntity : playerEntities) {
            SIMPLE_CHANNEL.sendTo(packet, playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static <P extends FnCPacket> void sendToServer(P objectToSend) {
        SIMPLE_CHANNEL.sendToServer(objectToSend);
    }

    public static <T extends FnCPacket> void handle(T packet, Supplier<NetworkEvent.Context> ctx, FnCPacket.Handle<T> handle) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> {
                Client.clientHandle(packet, handle);
            });
        } else {
            ServerPlayer sender = context.getSender();
            handle.handle(packet, sender != null ? sender.level : null, sender);
        }
        context.setPacketHandled(true);
    }

    private static class Client {
        private static <T extends FnCPacket> void clientHandle(T packet, FnCPacket.Handle<T> handle) {
            handle.handle(packet, Minecraft.getInstance().level, Minecraft.getInstance().player);
        }
    }
}