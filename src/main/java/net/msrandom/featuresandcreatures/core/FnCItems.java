package net.msrandom.featuresandcreatures.core;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

public class FnCItems {
    public static final DeferredRegister<Item> REGISTRAR = DeferredRegister.create(ForgeRegistries.ITEMS, FeaturesAndCreatures.MOD_ID);

    public static final ItemGroup TAB = new ItemGroup(FeaturesAndCreatures.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return Items.ACACIA_BOAT.getDefaultInstance();
        }
    };
}
