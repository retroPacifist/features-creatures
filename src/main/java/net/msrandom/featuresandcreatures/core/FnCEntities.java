package net.msrandom.featuresandcreatures.core;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.entity.Boar;
import net.msrandom.featuresandcreatures.entity.Jackalope;
import net.msrandom.featuresandcreatures.entity.Jockey;
import net.msrandom.featuresandcreatures.entity.Sabertooth;
import net.msrandom.featuresandcreatures.entity.Spear;

public class FnCEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRAR = DeferredRegister.create(ForgeRegistries.ENTITIES, FeaturesAndCreatures.MOD_ID);

    public static final RegistryObject<EntityType<Jockey>> JOCKEY = createMobEntity("jockey", EntityType.Builder.of(Jockey::new, MobCategory.CREATURE).sized(0.25f, 1f), 0xDBA5FF, 0x564237);
    public static final RegistryObject<EntityType<Boar>> BOAR = createMobEntity("boar", EntityType.Builder.of(Boar::new, MobCategory.CREATURE).sized(0.9F, 0.9F), 0x705F44, 0xFFF05A);
    public static final RegistryObject<EntityType<Jackalope>> JACKALOPE = createMobEntity("jackalope", EntityType.Builder.of(Jackalope::new, MobCategory.CREATURE).sized(1F, 0.8F), 0xB3A98D, 0x444444);
    public static final RegistryObject<EntityType<Sabertooth>> SABERTOOTH = createMobEntity("sabertooth", EntityType.Builder.of(Sabertooth::new, MobCategory.CREATURE).sized(1.2F, 1.3F), 0xC59125, 0xEEA2C4);

    public static final RegistryObject<EntityType<Spear>> SPEAR = createEntity("spear", EntityType.Builder.<Spear>of(Spear::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));


    private static <T extends Entity> RegistryObject<EntityType<T>> createMobEntity(String name, EntityType.Builder<T> builder, int colour1, int colour2) {
        EntityType<T> type = builder.build(name);
        FnCItems.REGISTRAR.register(name + "_spawn_egg", () -> new SpawnEggItem(type, colour1, colour2, new Item.Properties().tab(FnCItems.TAB)));
        return REGISTRAR.register(name, () -> type);
    }
    private static <T extends Entity> RegistryObject<EntityType<T>> createEntity(String name, EntityType.Builder<T> builder) {
        EntityType<T> type = builder.build(name);
        return REGISTRAR.register(name, () -> type);
    }

}