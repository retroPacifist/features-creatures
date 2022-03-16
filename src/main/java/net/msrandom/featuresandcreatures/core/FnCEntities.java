package net.msrandom.featuresandcreatures.core;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.entity.Jackalope;
import net.msrandom.featuresandcreatures.entity.Jockey;
import net.msrandom.featuresandcreatures.entity.Spear;
import net.msrandom.featuresandcreatures.entity.mount.Boar;
import net.msrandom.featuresandcreatures.entity.mount.Sabertooth;

public class FnCEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRAR = DeferredRegister.create(ForgeRegistries.ENTITIES, FeaturesAndCreatures.MOD_ID);

    public static final RegistryObject<EntityType<Jockey>> JOCKEY = REGISTRAR.register("jockey", () -> EntityType.Builder.of(Jockey::new, MobCategory.CREATURE).sized(0.25f, 1f).build("jockey"));
    public static final RegistryObject<EntityType<Boar>> BOAR = REGISTRAR.register("boar", () -> EntityType.Builder.of(Boar::new, MobCategory.CREATURE).sized(0.9F, 0.9F).build("boar"));
    public static final RegistryObject<EntityType<Jackalope>> JACKALOPE = REGISTRAR.register("jackalope", () -> EntityType.Builder.of(Jackalope::new, MobCategory.CREATURE).sized(1F, 0.8F).build("jackalope"));
    public static final RegistryObject<EntityType<Sabertooth>> SABERTOOTH = REGISTRAR.register("sabertooth", () -> EntityType.Builder.of(Sabertooth::new, MobCategory.CREATURE).sized(1.2F, 1.3F).build("sabertooth"));

    public static final RegistryObject<EntityType<Spear>> SPEAR = createEntity("spear", EntityType.Builder.<Spear>of(Spear::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));

    private static <T extends Entity> RegistryObject<EntityType<T>> createEntity(String name, EntityType.Builder<T> builder) {
        return REGISTRAR.register(name, () -> builder.build(name));
    }
}
