package net.msrandom.featuresandcreatures.common.item;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.core.FnCItems;

import java.util.function.Supplier;

public enum FnCArmorMaterial implements ArmorMaterial {

    ANTLER(new ResourceLocation(FeaturesAndCreatures.MOD_ID, "headdress"), 15, new int[]{0, 0, 0, 0},
            420, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F,
            () -> Ingredient.of(FnCItems.ANTLER.get())
    ),

    LUNAR(new ResourceLocation(FeaturesAndCreatures.MOD_ID, "lunar_headdress"), 15, new int[]{6, 6, 6, 6},
            11, SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 0.0F,
            () -> Ingredient.of(FnCItems.ANTLER.get()));

    private final ResourceLocation name;
    private final int durabilityAmount;
    private final int[] damageReductionAmountArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairMaterial;

    FnCArmorMaterial(ResourceLocation name, int durabilityAmount, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent, float resistance, float toughness, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.durabilityAmount = durabilityAmount;
        this.damageReductionAmountArray = damageReductionAmountArray;
        this.enchantability = enchantability;
        this.soundEvent = soundEvent;
        this.toughness = toughness;
        this.knockbackResistance = resistance;
        this.repairMaterial = Suppliers.memoize(repairMaterial::get);
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return durabilityAmount;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return damageReductionAmountArray[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.soundEvent;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }

    @Override
    public String getName() {
        return this.name.toString();
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}

