package com.github.corgitaco.networking;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Packet {

    void handle(Level level, Player player);

    abstract class Factory<T extends Packet> implements Serializer<T>, Deserializer<T> {
        @Getter
        private final Class<T> clazz;

        public Factory(Class<T> clazz) {
            this.clazz = clazz;
        }
    }

    interface Serializer<T extends Packet> extends BiConsumer<T, FriendlyByteBuf> {
    }

    interface Deserializer<T extends Packet> extends Function<FriendlyByteBuf, T> {
    }
}
