package net.msrandom.featuresandcreatures.common.item;

import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.block.FeaturesCreaturesBlocks;
import net.msrandom.featuresandcreatures.core.FnCEntities;

import java.util.function.Supplier;

// Please do not rename to "FnCItems"
@SuppressWarnings("unused")
@UtilityClass
public class FeaturesCreaturesItems {
    public final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, FeaturesAndCreatures.MOD_ID);

    // Spawn Eggs
    public final RegistryObject<Item> JOCKEY_SPAWN_EGG = REGISTER.register("jockey_spawn_egg", createSpawnEgg(FnCEntities.JOCKEY, 0xDBA5FF, 0x564237));
    public final RegistryObject<Item> BOAR_SPAWN_EGG = REGISTER.register("boar_spawn_egg", createSpawnEgg(FnCEntities.BOAR, 0x705F44, 0xFFF05A));
    public final RegistryObject<Item> JACKALOPE_SPAWN_EGG = REGISTER.register("jackalope_spawn_egg", createSpawnEgg(FnCEntities.JACKALOPE, 0xB3A98D, 0x444444));
    public final RegistryObject<Item> SABERTOOTH_SPAWN_EGG = REGISTER.register("sabertooth_spawn_egg", createSpawnEgg(FnCEntities.SABERTOOTH, 0xC59125, 0xEEA2C4));
    public final RegistryObject<Item> BLACK_FOREST_SPIRIT_SPAWN_EGG = REGISTER.register("black_forest_spirit_spawn_egg", createSpawnEgg(FnCEntities.BLACK_FOREST_SPIRIT, 0x392D22, 0xFFEC53));
    public final RegistryObject<Item> GUP_SPAWN_EGG = REGISTER.register("gup_spawn_egg", createSpawnEgg(FnCEntities.GUP, 0xDC793D, 0xFFD764));
    public final RegistryObject<Item> BRIMSTONE_GOLEM_SPAWN_EGG = REGISTER.register("brimstone_golem_spawn_egg", createSpawnEgg(FnCEntities.BRIMSTONE_GOLEM, 0x52491E, 0x5DF662));
    public final RegistryObject<Item> SHULKREN_YOUNGLING_SPAWN_EGG = REGISTER.register("shulkren_youngling_spawn_egg", createSpawnEgg(FnCEntities.SHULKREN_YOUNGLING, 0xE6E6E6, 0xA42CB4));
    public final RegistryObject<Item> TBH_SPAWN_EGG = REGISTER.register("tbh_spawn_egg", createSpawnEgg(FnCEntities.TBH, 0xFFFFFF, 0x000000));

    // Mob Loot
    public final RegistryObject<Item> ANTLER = REGISTER.register("antler", createItem());
    public final RegistryObject<Item> SABERTOOTH_FANG = REGISTER.register("sabertooth_fang", createItem());

    // Armor, Weapons, and Tools
    public final RegistryObject<Item> ANTLER_HEADDRESS = REGISTER.register("antler_headdress", () -> new AntlerHeaddressItem(FnCArmorMaterial.ANTLER, EquipmentSlot.HEAD, createProperties()));
    public final RegistryObject<Item> LUNAR_HEADDRESS = REGISTER.register("lunar_headdress", () -> new LunarHeaddressItem(FnCArmorMaterial.LUNAR, EquipmentSlot.HEAD, createProperties()));
    public final RegistryObject<Item> SPEAR = REGISTER.register("spear", () -> new SpearItem(createProperties()));
    //dawn spear goes here
    public final RegistryObject<Item> DOWSING_ROD = REGISTER.register("dowsing_rod", () -> new DowsingRodItem(createProperties()));
    //dawn dowser goes here
    //sunset dowser goes here
    //midnight dowser goes here

    // Blocks
    public final RegistryObject<Item> DAWN_ORE = REGISTER.register("dawn_ore", createBlockItem(FeaturesCreaturesBlocks.DAWN_ORE));
    public final RegistryObject<Item> STONE_DAWN_ORE = REGISTER.register("stone_dawn_ore", createBlockItem(FeaturesCreaturesBlocks.STONE_DAWN_ORE));
    public final RegistryObject<Item> DEEPSLATE_DAWN_ORE = REGISTER.register("deepslate_dawn_ore", createBlockItem(FeaturesCreaturesBlocks.DEEPSLATE_DAWN_ORE));
    public final RegistryObject<Item> DAWN_BLOCK = REGISTER.register("dawn_block", createBlockItem(FeaturesCreaturesBlocks.DAWN_BLOCK));
    public final RegistryObject<Item> SUNSET_ORE = REGISTER.register("sunset_ore", createBlockItem(FeaturesCreaturesBlocks.SUNSET_ORE));
    public final RegistryObject<Item> STONE_SUNSET_ORE = REGISTER.register("stone_sunset_ore", createBlockItem(FeaturesCreaturesBlocks.STONE_SUNSET_ORE));
    public final RegistryObject<Item> DEEPSLATE_SUNSET_ORE = REGISTER.register("deepslate_sunset_ore", createBlockItem(FeaturesCreaturesBlocks.DEEPSLATE_SUNSET_ORE));
    public final RegistryObject<Item> SUNSET_BLOCK = REGISTER.register("sunset_block", createBlockItem(FeaturesCreaturesBlocks.SUNSET_BLOCK));
    public final RegistryObject<Item> MIDNIGHT_ORE = REGISTER.register("midnight_ore", createBlockItem(FeaturesCreaturesBlocks.MIDNIGHT_ORE));
    public final RegistryObject<Item> STONE_MIDNIGHT_ORE = REGISTER.register("stone_midnight_ore", createBlockItem(FeaturesCreaturesBlocks.STONE_MIDNIGHT_ORE));
    public final RegistryObject<Item> DEEPSLATE_MIDNIGHT_ORE = REGISTER.register("deepslate_midnight_ore", createBlockItem(FeaturesCreaturesBlocks.DEEPSLATE_MIDNIGHT_ORE));
    public final RegistryObject<Item> MIDNIGHT_BLOCK = REGISTER.register("midnight_block", createBlockItem(FeaturesCreaturesBlocks.MIDNIGHT_BLOCK));

    // Misc.
    public final RegistryObject<Item> DAWN_CRYSTAL = REGISTER.register("dawn_crystal", createItem());
    public final RegistryObject<Item> SUNSET_CRYSTAL = REGISTER.register("sunset_crystal", createItem());
    public final RegistryObject<Item> MIDNIGHT_CRYSTAL = REGISTER.register("midnight_crystal", createItem());
    //tinted potion goes here

    // Hidden
    public final RegistryObject<Item> MEGA_POTION = REGISTER.register("mega_potion", () -> new Item(new Item.Properties()));
    public final RegistryObject<Item> BFS_ATTACK_ITEM = REGISTER.register("bfs_attack_item", () -> new Item(new Item.Properties()));

    public final RegistryObject<Item> BRIMSTONE = REGISTER.register("brimstone", createBlockItem(FeaturesCreaturesBlocks.BRIMSTONE));

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
        return () -> new ForgeSpawnEggItem(object, base, spots, createProperties());
    }
}
