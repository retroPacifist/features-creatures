package retropacifist.featuresandcreatures.network;

import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.network.AntlerHeaddressChargePacket;
import retropacifist.featuresandcreatures.common.network.JockeyPosPacket;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static retropacifist.featuresandcreatures.network.FnCPacket.PacketDirection.CLIENT_TO_SERVER;
import static retropacifist.featuresandcreatures.network.FnCPacket.PacketDirection.SERVER_TO_CLIENT;

public interface FnCPacket {
    Map<String, Handler<?>> S2C_PACKETS = Util.make(new HashMap<>(), map -> {
        FeaturesAndCreatures.LOGGER.info("Initializing Features & Creature network...");
        map.put("jockey_pos_packet", new Handler<>(JockeyPosPacket.class, SERVER_TO_CLIENT, JockeyPosPacket::write, JockeyPosPacket::readFromPacket, JockeyPosPacket::handle));
        map.put("jockey_pos_packet", new Handler<>(AntlerHeaddressChargePacket.class, CLIENT_TO_SERVER, AntlerHeaddressChargePacket::write, AntlerHeaddressChargePacket::new, AntlerHeaddressChargePacket::handle));
        FeaturesAndCreatures.LOGGER.info("Initialized Features & Creature network!");
    });


    void write(FriendlyByteBuf buf);

    void handle(@Nullable Level level, @Nullable Player player);

    record Handler<T extends FnCPacket>(Class<T> clazz, PacketDirection direction, BiConsumer<T, FriendlyByteBuf> write,
                                        Function<FriendlyByteBuf, T> read,
                                        Handle<T> handle) {
    }

    enum PacketDirection {
        SERVER_TO_CLIENT,
        CLIENT_TO_SERVER
    }

    @FunctionalInterface
    interface Handle<T extends FnCPacket> {
        void handle(T packet, Level level, Player player);
    }
}
