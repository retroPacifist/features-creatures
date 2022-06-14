package retropacifist.featuresandcreatures.network;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class NetworkHandler {
    private final Map<Class<? extends Packet>, Pair<ResourceLocation, Packet.Serializer<Packet>>> MAP = new ConcurrentHashMap<>();

    public <T extends Packet> void register(ResourceLocation location, Packet.Factory<Packet> factory) {
        MAP.put(factory.getClazz(), new ObjectObjectImmutablePair<>(location, factory));

        getProxy().register(location, factory);
    }

    public Proxy getProxy() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? ClientProxy.INSTANCE : ServerProxy.INSTANCE;
    }

    private sealed interface Proxy {

        default <T extends Packet> void sendToPlayer(ServerPlayer player, T packet) {
            throw new UnsupportedOperationException("Unable to send to a server player on the client.");
        }

        default <T extends Packet> void sendToPlayers(List<ServerPlayer> list, T packet) {
            throw new UnsupportedOperationException("Unable to send to server players on the client.");
        }

        <T extends Packet> void register(ResourceLocation location, Packet.Deserializer<T> deserializer);
    }

    private static final class ClientProxy implements Proxy {
        private static final Proxy INSTANCE = new ClientProxy();

        @Override
        public <T extends Packet> void register(ResourceLocation location, Packet.Deserializer<T> deserializer) {
            ClientPlayNetworking.registerGlobalReceiver(location, (client, handler, buf, responseSender) -> {
                T packet = deserializer.apply(buf);

                client.execute(() -> {
                    @Nullable
                    Level level = client.level;
                    if (level != null) {
                        packet.handle(level, client.player);
                    }
                });
            });
        }
    }

    private static final class ServerProxy implements Proxy {
        private static final Proxy INSTANCE = new ServerProxy();

        @Override
        public <T extends Packet> void sendToPlayer(ServerPlayer player, T packet) {
            Pair<ResourceLocation, Packet.Serializer<Packet>> pair = MAP.get(packet.getClass());

            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            pair.right().accept(packet, buf);

            ServerPlayNetworking.send(player, pair.first(), buf);
        }

        @Override
        public <T extends Packet> void sendToPlayers(List<ServerPlayer> list, T packet) {
            list.forEach(player -> sendToPlayer(player, packet));
        }

        @Override
        public <T extends Packet> void register(ResourceLocation location, Packet.Deserializer<T> deserializer) {
            ServerPlayNetworking.registerGlobalReceiver(location, (server, player, handler, buf, responseSender) -> {
                T packet = deserializer.apply(buf);

                server.execute(() -> {
                    @Nullable
                    Level level = player.level;
                    if (level != null) {
                        packet.handle(level, player);
                    }
                });
            });
        }
    }
}
