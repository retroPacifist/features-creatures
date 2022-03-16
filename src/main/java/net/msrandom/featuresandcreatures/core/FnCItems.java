package net.msrandom.featuresandcreatures.core;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.item.AntlerHeaddressItem;
import net.msrandom.featuresandcreatures.item.DowsingRodItem;
import net.msrandom.featuresandcreatures.item.SpearItem;

public class FnCItems {
    public static final DeferredRegister<Item> REGISTRAR = DeferredRegister.create(ForgeRegistries.ITEMS, FeaturesAndCreatures.MOD_ID);
    public static final CreativeModeTab TAB = new CreativeModeTab(FeaturesAndCreatures.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return FnCItems.MEGA_POTION.get().getDefaultInstance();
        }
    };

    public static final RegistryObject<Item> MEGA_POTION = REGISTRAR.register("mega_potion", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANTLER = REGISTRAR.register("antler", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> ANTLER_HEADDRESS = REGISTRAR.register("antler_headdress", () -> new AntlerHeaddressItem(EquipmentSlot.HEAD, new Item.Properties().tab(TAB).durability(-1)));
    public static final RegistryObject<Item> SABERTOOTH_FANG = REGISTRAR.register("sabertooth_fang", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> SPEAR = REGISTRAR.register("spear", () -> new SpearItem(new Item.Properties().durability(200).tab(TAB)));
    public static final RegistryObject<Item> DOWSING_ROD = REGISTRAR.register("dowsing_rod", () -> new DowsingRodItem(new Item.Properties().tab(TAB)));

    public static final RegistryObject<Item> JOCKEY_SPAWN_EGG = REGISTRAR.register("jockey_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.JOCKEY,0xDBA5FF, 0x564237, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> BOAR_SPAWN_EGG = REGISTRAR.register("boar_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.BOAR,0x705F44, 0xFFF05A, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> SABERTOOTH_SPAWN_EGG = REGISTRAR.register("sabertooth_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.SABERTOOTH, 0xC59125, 0xEEA2C4, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> JACKALOPE_SPAWN_EGG = REGISTRAR.register("jackalope_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.JACKALOPE,0xB3A98D, 0x444444, new Item.Properties().tab(FnCItems.TAB)));

}
