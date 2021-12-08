package net.msrandom.featuresandcreatures.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.msrandom.featuresandcreatures.common.items.antler_headdress.AntlerHeaddressItem;

import java.util.Optional;
import java.util.function.Supplier;

public class AntlerHeaddressChargePacket {
    private final int charge;

    private AntlerHeaddressChargePacket(PacketBuffer buffer) {
        charge = buffer.readVarInt();
    }

    public AntlerHeaddressChargePacket(int charge) {
        this.charge = charge;
    }

    private void write(PacketBuffer buffer) {
        buffer.writeVarInt(charge);
    }

    private void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayerEntity player = contextSupplier.get().getSender();
            if (player != null) {
                ItemStack stack = player.getItemBySlot(EquipmentSlotType.HEAD);
                Item item = stack.getItem();
                // Check cooldown again, in case the player is cheating
                if (item instanceof AntlerHeaddressItem && !player.getCooldowns().isOnCooldown(item)) {
                    ((AntlerHeaddressItem) item).handleCharge(player, stack, charge);
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
