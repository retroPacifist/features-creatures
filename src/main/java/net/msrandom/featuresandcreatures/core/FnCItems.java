package net.msrandom.featuresandcreatures.core;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.items.SpearItem;
import net.msrandom.featuresandcreatures.common.items.materials.HeadDressMaterial;

public class FnCItems {
    public static final DeferredRegister<Item> REGISTRAR = DeferredRegister.create(ForgeRegistries.ITEMS, FeaturesAndCreatures.MOD_ID);
    public static final RegistryObject<Item> MEGA_POTION = REGISTRAR.register("mega_potion", () -> new Item(new Item.Properties()));
    public static final ItemGroup TAB = new ItemGroup(FeaturesAndCreatures.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return FnCItems.MEGA_POTION.get().getDefaultInstance();
        }
    };
    public static final RegistryObject<Item> ANTLER = REGISTRAR.register("antler", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> ANTLER_HEADDRESS = REGISTRAR.register("antler_headdress", () -> new ArmorItem(HeadDressMaterial.HEAD_DRESS, EquipmentSlotType.HEAD, new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> SABERTOOTH_FANG = REGISTRAR.register("sabertooth_fang", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> SPEAR_ITEM = REGISTRAR.register("spear_item", () -> new SpearItem(new Item.Properties().durability(350).tab(TAB)));
}