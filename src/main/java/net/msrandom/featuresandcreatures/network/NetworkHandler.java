package net.msrandom.featuresandcreatures.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
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

    public static void sendToClient(ServerPlayerEntity playerEntity, Object objectToSend) {
        SIMPLE_CHANNEL.sendTo(objectToSend, playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToAllClients(List<ServerPlayerEntity> playerEntities, Object objectToSend) {
        for (ServerPlayerEntity playerEntity : playerEntities) {
            SIMPLE_CHANNEL.sendTo(objectToSend, playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendToServer(Object objectToSend) {
        SIMPLE_CHANNEL.sendToServer(objectToSend);
    }
}