package net.msrandom.featuresandcreatures.item;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class FnCArmorMaterial implements ArmorMaterial {
    private final ResourceLocation name;
    private final int[] durabilityAmount;
    private final int[] damageReductionAmountArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairMaterial;

    public FnCArmorMaterial(ResourceLocation name, int[] durabilityAmount, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent, float resistance, float toughness, Supplier<Ingredient> repairMaterial) {
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
        return durabilityAmount[slot.getIndex()];
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

