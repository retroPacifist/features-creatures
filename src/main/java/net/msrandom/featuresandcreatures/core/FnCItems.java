package net.msrandom.featuresandcreatures.core;

import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

public class FnCItems {
    public static final DeferredRegister<Item> REGISTRAR = DeferredRegister.create(ForgeRegistries.ITEMS, FeaturesAndCreatures.MOD_ID);

    public static final ItemGroup TAB = new ItemGroup(FeaturesAndCreatures.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return FnCItems.MEGA_POTION.get().getDefaultInstance();
        }
    };

    public static final RegistryObject<Item> MEGA_POTION = REGISTRAR.register("mega_potion", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANTLER = REGISTRAR.register("antler", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> ANTLER_HEADDRESS = REGISTRAR.register("antler_headdress", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> SABERTOOTH_FANG = REGISTRAR.register("sabertooth_fang", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> SPEAR_ITEM = REGISTRAR.register("spear_item", () -> new Item(new Item.Properties().tab(TAB)));
}