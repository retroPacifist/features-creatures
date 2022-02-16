package net.msrandom.featuresandcreatures.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

import java.util.List;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel SIMPLE_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(FeaturesAndCreatures.MOD_ID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        AntlerHeaddressChargePacket.register(0, NetworkHandler.SIMPLE_CHANNEL);
        SIMPLE_CHANNEL.registerMessage(1, JockeyPosPacket.class, JockeyPosPacket::writeToPacket, JockeyPosPacket::readFromPacket, JockeyPosPacket::handle);
    }

    public static void sendToClient(ServerPlayer playerEntity, Object objectToSend) {
        SIMPLE_CHANNEL.sendTo(objectToSend, playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToAllClients(List<ServerPlayer> playerEntities, Object objectToSend) {
        for (ServerPlayer playerEntity : playerEntities) {
            SIMPLE_CHANNEL.sendTo(objectToSend, playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendToServer(Object objectToSend) {
        SIMPLE_CHANNEL.sendToServer(objectToSend);
    }
}
