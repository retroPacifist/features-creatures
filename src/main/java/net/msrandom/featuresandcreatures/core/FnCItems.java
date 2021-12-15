package net.msrandom.featuresandcreatures.core;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.item.AntlerHeaddressItem;
import net.msrandom.featuresandcreatures.item.SpearItem;
import org.jetbrains.annotations.NotNull;

public class FnCItems {
    public static final DeferredRegister<Item> REGISTRAR = DeferredRegister.create(ForgeRegistries.ITEMS, FeaturesAndCreatures.MOD_ID);
    public static final RegistryObject<Item> MEGA_POTION = REGISTRAR.register("mega_potion", () -> new Item(new Item.Properties()));
    public static final CreativeModeTab TAB = new CreativeModeTab(FeaturesAndCreatures.MOD_ID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(FnCItems.MEGA_POTION.get());
        }
    };
    public static final RegistryObject<Item> ANTLER = REGISTRAR.register("antler", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> ANTLER_HEADDRESS = REGISTRAR.register("antler_headdress", () -> new AntlerHeaddressItem(EquipmentSlot.HEAD, new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> SABERTOOTH_FANG = REGISTRAR.register("sabertooth_fang", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> SPEAR = REGISTRAR.register("spear", () -> new SpearItem(new Item.Properties().durability(200).tab(TAB)));
}