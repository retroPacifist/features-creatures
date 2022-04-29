package net.msrandom.featuresandcreatures.core;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.block.FeaturesCreaturesBlocks;
import net.msrandom.featuresandcreatures.common.item.AntlerHeaddressItem;
import net.msrandom.featuresandcreatures.common.item.DowsingRodItem;
import net.msrandom.featuresandcreatures.common.item.SpearItem;

public class FnCItems {
    public static final DeferredRegister<Item> REGISTRAR = DeferredRegister.create(ForgeRegistries.ITEMS, FeaturesAndCreatures.MOD_ID);
    public static final CreativeModeTab TAB = new CreativeModeTab(FeaturesAndCreatures.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return FnCItems.MEGA_POTION.get().getDefaultInstance();
        }
    };

    public static final RegistryObject<Item> MEGA_POTION = REGISTRAR.register("mega_potion", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BFS_ATTACK_ITEM = REGISTRAR.register("bfs_attack_item", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TINTED_BOTTLE = REGISTRAR.register("tinted_bottle", () -> new BottleItem(new Item.Properties().tab(TAB)));

    public static final RegistryObject<Item> ANTLER = REGISTRAR.register("antler", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> ANTLER_HEADDRESS = REGISTRAR.register("antler_headdress", () -> new AntlerHeaddressItem(EquipmentSlot.HEAD, new Item.Properties().tab(TAB).durability(-1)));
    public static final RegistryObject<Item> SABERTOOTH_FANG = REGISTRAR.register("sabertooth_fang", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> DAWN_CRYSTAL = REGISTRAR.register("dawn_crystal", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> MIDNIGHT_CRYSTAL = REGISTRAR.register("midnight_crystal", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> SUNSET_CRYSTAL = REGISTRAR.register("sunset_crystal", () -> new Item(new Item.Properties().tab(TAB)));
    public static final RegistryObject<Item> SPEAR = REGISTRAR.register("spear", () -> new SpearItem(new Item.Properties().durability(200).tab(TAB)));
    public static final RegistryObject<Item> DOWSING_ROD = REGISTRAR.register("dowsing_rod", () -> new DowsingRodItem(new Item.Properties().tab(TAB)));

    public static final RegistryObject<Item> JOCKEY_SPAWN_EGG = REGISTRAR.register("jockey_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.JOCKEY,0xDBA5FF, 0x564237, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> BOAR_SPAWN_EGG = REGISTRAR.register("boar_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.BOAR,0x705F44, 0xFFF05A, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> SABERTOOTH_SPAWN_EGG = REGISTRAR.register("sabertooth_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.SABERTOOTH, 0xC59125, 0xEEA2C4, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> JACKALOPE_SPAWN_EGG = REGISTRAR.register("jackalope_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.JACKALOPE,0xB3A98D, 0x444444, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> BLACK_FOREST_SPIRIT_SPAWN_EGG = REGISTRAR.register("black_forest_spirit_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.BLACK_FOREST_SPIRIT,0x392D22, 0xFFEC53, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> GUP_SPAWN_EGG = REGISTRAR.register("gup_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.GUP,0xDC793D, 0xFFD764, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> BRIMSTONE_GOLEM_SPAWN_EGG = REGISTRAR.register("brimstone_golem_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.BRIMSTONE_GOLEM,0x52491E, 0x5DF662, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> SHULKREN_YOUNGLING_SPAWN_EGG = REGISTRAR.register("shulkren_youngling_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.SHULKREN_YOUNGLING,0xE6E6E6, 0xA42CB4, new Item.Properties().tab(FnCItems.TAB)));
    public static final RegistryObject<Item> TBH_SPAWN_EGG = REGISTRAR.register("tbh_spawn_egg", () -> new ForgeSpawnEggItem(FnCEntities.TBH,0xFFFFFF, 0x000000, new Item.Properties().tab(FnCItems.TAB)));

    public static final RegistryObject<Item> DAWN_ORE = createBlockItem(FeaturesCreaturesBlocks.DAWN_ORE);
    public static final RegistryObject<Item> SUNSET_ORE = createBlockItem(FeaturesCreaturesBlocks.SUNSET_ORE);
    public static final RegistryObject<Item> MIDNIGHT_ORE = createBlockItem(FeaturesCreaturesBlocks.MIDNIGHT_ORE);

    public static final RegistryObject<Item> DEEPSLATE_DAWN_ORE = createBlockItem(FeaturesCreaturesBlocks.DEEPSLATE_DAWN_ORE);
    public static final RegistryObject<Item> DEEPSLATE_SUNSET_ORE = createBlockItem(FeaturesCreaturesBlocks.DEEPSLATE_SUNSET_ORE);
    public static final RegistryObject<Item> DEEPSLATE_MIDNIGHT_ORE = createBlockItem(FeaturesCreaturesBlocks.DEEPSLATE_MIDNIGHT_ORE);

    public static final RegistryObject<Item> DAWN_BLOCK = createBlockItem(FeaturesCreaturesBlocks.DAWN_BLOCK);
    public static final RegistryObject<Item> SUNSET_BLOCK = createBlockItem(FeaturesCreaturesBlocks.SUNSET_BLOCK);
    public static final RegistryObject<Item> MIDNIGHT_BLOCK = createBlockItem(FeaturesCreaturesBlocks.MIDNIGHT_BLOCK);

    public static RegistryObject<Item> createBlockItem(RegistryObject<Block> block){
        return REGISTRAR.register(block.getId().getPath(), ()-> new BlockItem(block.get(), new Item.Properties().tab(TAB)));
    }
}