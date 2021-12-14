package net.msrandom.featuresandcreatures.core;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.client.renderer.entity.SpearRenderer;
import net.msrandom.featuresandcreatures.client.renderer.item.SpearItemRenderer;
import net.msrandom.featuresandcreatures.item.AntlerHeaddressItem;
import net.msrandom.featuresandcreatures.item.DowsingRodItem;
import net.msrandom.featuresandcreatures.item.SpearItem;
import net.msrandom.featuresandcreatures.util.FnCRegistrar;

public class FnCItems {
    public static final FnCRegistrar<Item> REGISTRAR = new FnCRegistrar<>(ForgeRegistries.ITEMS);
    public static final ItemGroup TAB = new ItemGroup(FeaturesAndCreatures.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return FnCItems.MEGA_POTION.getDefaultInstance();
        }
    };

    public static final Item MEGA_POTION = REGISTRAR.add("mega_potion", new Item(new Item.Properties()));
    public static final Item ANTLER = REGISTRAR.add("antler", new Item(new Item.Properties().tab(TAB)));
    public static final Item ANTLER_HEADDRESS = REGISTRAR.add("antler_headdress", new AntlerHeaddressItem(EquipmentSlotType.HEAD, new Item.Properties().tab(TAB)));
    public static final Item SABERTOOTH_FANG = REGISTRAR.add("sabertooth_fang", new Item(new Item.Properties().tab(TAB)));
    public static final Item SPEAR = REGISTRAR.add("spear", new SpearItem(new Item.Properties().durability(200).tab(TAB).setISTER(() -> SpearItemRenderer::new)));
    public static final Item DOWSING_ROD = REGISTRAR.add("dowsing_rod", new DowsingRodItem(new Item.Properties().tab(TAB)));
}
