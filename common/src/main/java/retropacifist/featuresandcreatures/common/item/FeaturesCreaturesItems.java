package retropacifist.featuresandcreatures.common.item;

import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.block.FeaturesCreaturesBlocks;
import retropacifist.featuresandcreatures.core.FnCEntities;
import retropacifist.featuresandcreatures.reg.RegistrationProvider;
import retropacifist.featuresandcreatures.reg.RegistryObject;

import java.util.function.Supplier;

// Please do not rename to "FnCItems"
@SuppressWarnings("unused")
@UtilityClass
public class FeaturesCreaturesItems {
    public static final RegistrationProvider<Item> REGISTER = RegistrationProvider.get(Registry.ITEM, FeaturesAndCreatures.MOD_ID);

    // Spawn Eggs
    public static final RegistryObject<Item> JOCKEY_SPAWN_EGG = REGISTER.register("jockey_spawn_egg", createSpawnEgg(FnCEntities.JOCKEY, 0xDBA5FF, 0x564237));
    public static final RegistryObject<Item> BOAR_SPAWN_EGG = REGISTER.register("boar_spawn_egg", createSpawnEgg(FnCEntities.BOAR, 0x705F44, 0xFFF05A));
    public static final RegistryObject<Item> JACKALOPE_SPAWN_EGG = REGISTER.register("jackalope_spawn_egg", createSpawnEgg(FnCEntities.JACKALOPE, 0xB3A98D, 0x444444));
    public static final RegistryObject<Item> SABERTOOTH_SPAWN_EGG = REGISTER.register("sabertooth_spawn_egg", createSpawnEgg(FnCEntities.SABERTOOTH, 0xC59125, 0xEEA2C4));
    public static final RegistryObject<Item> BLACK_FOREST_SPIRIT_SPAWN_EGG = REGISTER.register("black_forest_spirit_spawn_egg", createSpawnEgg(FnCEntities.BLACK_FOREST_SPIRIT, 0x392D22, 0xFFEC53));
    public static final RegistryObject<Item> GUP_SPAWN_EGG = REGISTER.register("gup_spawn_egg", createSpawnEgg(FnCEntities.GUP, 0xDC793D, 0xFFD764));
    public static final RegistryObject<Item> BRIMSTONE_GOLEM_SPAWN_EGG = REGISTER.register("brimstone_golem_spawn_egg", createSpawnEgg(FnCEntities.BRIMSTONE_GOLEM, 0x52491E, 0x5DF662));
    public static final RegistryObject<Item> SHULKREN_YOUNGLING_SPAWN_EGG = REGISTER.register("shulkren_youngling_spawn_egg", createSpawnEgg(FnCEntities.SHULKREN_YOUNGLING, 0xE6E6E6, 0xA42CB4));
    public static final RegistryObject<Item> TBH_SPAWN_EGG = REGISTER.register("tbh_spawn_egg", createSpawnEgg(FnCEntities.TBH, 0xFFFFFF, 0x000000));

    // Mob Loot
    public static final RegistryObject<Item> ANTLER = REGISTER.register("antler", createItem());
    public static final RegistryObject<Item> SABERTOOTH_FANG = REGISTER.register("sabertooth_fang", createItem());

    // Armor, Weapons, and Tools
    public static final RegistryObject<? extends ArmorItem> ANTLER_HEADDRESS = REGISTER.register("antler_headdress", () -> PlatformItemHandler.INSTANCE.getAntlerHeaddressItem(FnCArmorMaterial.ANTLER, EquipmentSlot.HEAD, createProperties()));
    public static final RegistryObject<? extends ArmorItem> LUNAR_HEADDRESS = REGISTER.register("lunar_headdress", () -> PlatformItemHandler.INSTANCE.getLunarHeaddressItem(FnCArmorMaterial.LUNAR, EquipmentSlot.HEAD, createProperties()));
    public static final RegistryObject<Item> SPEAR = REGISTER.register("spear", () -> new SpearItem(createProperties()));
    //dawn spear goes here
    public static final RegistryObject<Item> DOWSING_ROD = REGISTER.register("dowsing_rod", () -> new DowsingRodItem(createProperties()));
    //dawn dowser goes here
    //sunset dowser goes here
    //midnight dowser goes here

    // Blocks
    public static final RegistryObject<Item> DAWN_ORE = REGISTER.register("dawn_ore", createBlockItem(FeaturesCreaturesBlocks.DAWN_ORE));
    public static final RegistryObject<Item> STONE_DAWN_ORE = REGISTER.register("stone_dawn_ore", createBlockItem(FeaturesCreaturesBlocks.STONE_DAWN_ORE));
    public static final RegistryObject<Item> DEEPSLATE_DAWN_ORE = REGISTER.register("deepslate_dawn_ore", createBlockItem(FeaturesCreaturesBlocks.DEEPSLATE_DAWN_ORE));
    public static final RegistryObject<Item> DAWN_BLOCK = REGISTER.register("dawn_block", createBlockItem(FeaturesCreaturesBlocks.DAWN_BLOCK));
    public static final RegistryObject<Item> SUNSET_ORE = REGISTER.register("sunset_ore", createBlockItem(FeaturesCreaturesBlocks.SUNSET_ORE));
    public static final RegistryObject<Item> STONE_SUNSET_ORE = REGISTER.register("stone_sunset_ore", createBlockItem(FeaturesCreaturesBlocks.STONE_SUNSET_ORE));
    public static final RegistryObject<Item> DEEPSLATE_SUNSET_ORE = REGISTER.register("deepslate_sunset_ore", createBlockItem(FeaturesCreaturesBlocks.DEEPSLATE_SUNSET_ORE));
    public static final RegistryObject<Item> SUNSET_BLOCK = REGISTER.register("sunset_block", createBlockItem(FeaturesCreaturesBlocks.SUNSET_BLOCK));
    public static final RegistryObject<Item> MIDNIGHT_ORE = REGISTER.register("midnight_ore", createBlockItem(FeaturesCreaturesBlocks.MIDNIGHT_ORE));
    public static final RegistryObject<Item> STONE_MIDNIGHT_ORE = REGISTER.register("stone_midnight_ore", createBlockItem(FeaturesCreaturesBlocks.STONE_MIDNIGHT_ORE));
    public static final RegistryObject<Item> DEEPSLATE_MIDNIGHT_ORE = REGISTER.register("deepslate_midnight_ore", createBlockItem(FeaturesCreaturesBlocks.DEEPSLATE_MIDNIGHT_ORE));
    public static final RegistryObject<Item> MIDNIGHT_BLOCK = REGISTER.register("midnight_block", createBlockItem(FeaturesCreaturesBlocks.MIDNIGHT_BLOCK));

    // Misc.
    public static final RegistryObject<Item> DAWN_CRYSTAL = REGISTER.register("dawn_crystal", createItem());
    public static final RegistryObject<Item> SUNSET_CRYSTAL = REGISTER.register("sunset_crystal", createItem());
    public static final RegistryObject<Item> MIDNIGHT_CRYSTAL = REGISTER.register("midnight_crystal", createItem());
    //tinted potion goes here

    // Hidden
    public static final RegistryObject<Item> MEGA_POTION = REGISTER.register("mega_potion", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BFS_ATTACK_ITEM = REGISTER.register("bfs_attack_item", () -> new Item(new Item.Properties()));

    private Item.Properties createProperties() {
        return new Item.Properties().tab(FeaturesCreaturesTabs.GENERAL);
    }

    private Supplier<Item> createBlockItem(RegistryObject<Block> object) {
        return () -> new BlockItem(object.get(), createProperties());
    }

    private Supplier<Item> createItem() {
        return () -> new Item(createProperties());
    }

    private <T extends Mob> Supplier<Item> createSpawnEgg(RegistryObject<EntityType<T>> object, int base, int spots) {
        return () -> PlatformItemHandler.INSTANCE.getSpawnEggItem(object, base, spots, createProperties());
    }

    public static void init() {

    }
}
