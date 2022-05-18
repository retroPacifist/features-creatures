package retropacifist.featuresandcreatures.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import retropacifist.featuresandcreatures.common.item.AntlerHeaddressItem;
import retropacifist.featuresandcreatures.network.FnCPacket;

import javax.annotation.Nullable;

public class AntlerHeaddressChargePacket implements FnCPacket {
    private final int charge;

    public AntlerHeaddressChargePacket(FriendlyByteBuf buffer) {
        charge = buffer.readVarInt();
    }

    public AntlerHeaddressChargePacket(int charge) {
        this.charge = charge;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(charge);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (player != null) {
            Item item = player.getItemBySlot(EquipmentSlot.HEAD).getItem();
            // Check cooldown again, in case the player is cheating
            if (item instanceof AntlerHeaddressItem headdress && !player.getCooldowns().isOnCooldown(item)) {
                headdress.handleCharge(player, charge);
            }
        }
    }
}
