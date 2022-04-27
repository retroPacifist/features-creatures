package net.msrandom.featuresandcreatures.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import net.msrandom.featuresandcreatures.common.item.AntlerHeaddressItem;

import java.util.Optional;
import java.util.function.Supplier;

public class AntlerHeaddressChargePacket {
    private final int charge;

    private AntlerHeaddressChargePacket(FriendlyByteBuf buffer) {
        charge = buffer.readVarInt();
    }

    public AntlerHeaddressChargePacket(int charge) {
        this.charge = charge;
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(charge);
    }

    private void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null) {
                Item item = player.getItemBySlot(EquipmentSlot.HEAD).getItem();
                // Check cooldown again, in case the player is cheating
                if (item instanceof AntlerHeaddressItem && !player.getCooldowns().isOnCooldown(item)) {
                    ((AntlerHeaddressItem) item).handleCharge(player, charge);
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public static void register(int index, SimpleChannel channel) {
        channel.registerMessage(
                index,
                AntlerHeaddressChargePacket.class,
                AntlerHeaddressChargePacket::write,
                AntlerHeaddressChargePacket::new,
                AntlerHeaddressChargePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }
}
