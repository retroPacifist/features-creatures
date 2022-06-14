package net.msrandom.featuresandcreatures.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.msrandom.featuresandcreatures.core.FnCTriggers;
import net.msrandom.featuresandcreatures.network.IChargeHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class AntlerHeaddressItem extends ArmorItem implements IAnimatable {

    private static final String DATA_PREFIX = "AntlerHeaddress";
    public static final String CURRENT_CHARGE = DATA_PREFIX + "Charge";
    protected static final String LAST_CHARGE = DATA_PREFIX + "LastCharge";
    protected static final String IS_DAMAGING = DATA_PREFIX + "IsDamaging";
    protected static final String DAMAGE_TIMER = DATA_PREFIX + "DamageTimer";
    private final AnimationFactory factory = new AnimationFactory(this);

    public AntlerHeaddressItem(ArmorMaterial material, EquipmentSlot slot, Item.Properties builder) {
        super(material, slot, builder);
    }

    @Override
    public void registerControllers(AnimationData data) {
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public float knockbackValue() {
        return 0.3F;
    }

    public static void usedHeaddress(ServerPlayer entity, Level world, ItemStack stack) {
        if (!world.isClientSide) {
            FnCTriggers.USE_ANTLER.trigger(entity, stack);
        }
        entity.awardStat(Stats.ITEM_USED.get(stack.getItem()));
    }

    public float getDamageAmount(int oldCharge) {
        return Math.round(oldCharge / (getMaxCharge() * 0.202f));
    }

    public int getMaxCharge() {
        return 100;
    }

    public void handleCharge(Player player, int charge) {
        player.getCooldowns().addCooldown(this, 40);
        Vec3 look = player.getLookAngle();
        CompoundTag persistentData = ((IChargeHandler) player).getPersistentData();
        if (charge > Math.round(getMaxCharge() * 0.37f)) {
            double amount = charge / (getMaxCharge() * 0.37);
            player.push(look.x * amount, 0.1, look.z * amount);
            if (!player.level.isClientSide) {
                persistentData.putBoolean(IS_DAMAGING, true);
                persistentData.putInt(LAST_CHARGE, charge);
            }
        } else if (charge >= 1) {
            player.push(look.x * (0.5), 0.1, look.z * (0.5));
            if (!player.level.isClientSide)
                persistentData.putInt(LAST_CHARGE, charge);
        }
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return ImmutableMultimap.of();
    }
}
