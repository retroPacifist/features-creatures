package net.msrandom.featuresandcreatures.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.core.FnCItems;

public class LunarHeaddressItem extends AntlerHeaddressItem{
    private static final ArmorMaterial MATERIAL = new FnCArmorMaterial(
            new ResourceLocation(FeaturesAndCreatures.MOD_ID, "lunar_headdress"),
            new int[]{6, 111, 111, 37},
            new int[]{0, 0, 0, 0},
            420,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            () -> Ingredient.of(FnCItems.ANTLER.get())
    );

    public LunarHeaddressItem(Properties builder) {
        super(MATERIAL, EquipmentSlot.HEAD, builder);
    }

    @Override
    public float getDamageAmount(int oldCharge) {
        return oldCharge / (getMaxCharge() * 0.102f);
    }
}
