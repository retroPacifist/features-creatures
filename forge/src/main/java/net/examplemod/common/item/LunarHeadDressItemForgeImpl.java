package net.examplemod.common.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;

public class LunarHeadDressItemForgeImpl extends AntlerHeaddressItemForgeImpl {
    public LunarHeadDressItemForgeImpl(ArmorMaterial material, EquipmentSlot slot, Properties builder) {
        super(material, slot, builder);
    }

    @Override
    public float getDamageAmount(int oldCharge) {
        return Math.round(oldCharge / (getMaxCharge() * 0.102f));
    }

    @Override
    public float knockbackValue() {
        return 0.5F;
    }
}
