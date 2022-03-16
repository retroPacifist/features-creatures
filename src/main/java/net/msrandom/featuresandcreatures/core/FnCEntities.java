package net.msrandom.featuresandcreatures.core;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.entity.BlackForestSpirit;
import net.msrandom.featuresandcreatures.entity.Gup;
import net.msrandom.featuresandcreatures.entity.Jockey;
import net.msrandom.featuresandcreatures.entity.Spear;
import net.msrandom.featuresandcreatures.entity.mount.Boar;
import net.msrandom.featuresandcreatures.entity.mount.Jackalope;
import net.msrandom.featuresandcreatures.entity.mount.Sabertooth;

public class FnCEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRAR = DeferredRegister.create(ForgeRegistries.ENTITIES, FeaturesAndCreatures.MOD_ID);

    public static final RegistryObject<EntityType<Jockey>> JOCKEY = createEntity("jockey", EntityType.Builder.of(Jockey::new, MobCategory.CREATURE).sized(0.25f, 1f));
    public static final RegistryObject<EntityType<Boar>> BOAR = createEntity("boar", EntityType.Builder.of(Boar::new, MobCategory.CREATURE).sized(0.9F, 0.9F));
    public static final RegistryObject<EntityType<Jackalope>> JACKALOPE = createEntity("jackalope", EntityType.Builder.of(Jackalope::new, MobCategory.CREATURE).sized(1F, 0.8F));
    public static final RegistryObject<EntityType<Sabertooth>> SABERTOOTH = createEntity("sabertooth", EntityType.Builder.of(Sabertooth::new, MobCategory.CREATURE).sized(1.2F, 1.3F));
    public static final RegistryObject<EntityType<BlackForestSpirit>> BLACK_FOREST_SPIRIT = createEntity("black_forest_spirit", EntityType.Builder.of(BlackForestSpirit::new, MobCategory.CREATURE).sized(0.7F, 2f));
    public static final RegistryObject<EntityType<Gup>> GUP = createEntity("gup", EntityType.Builder.of(Gup::new, MobCategory.CREATURE).sized(2.6F, 2f));

    public static final RegistryObject<EntityType<Spear>> SPEAR = createEntity("spear", EntityType.Builder.<Spear>of(Spear::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));

    private static <T extends Entity> RegistryObject<EntityType<T>> createEntity(String name, EntityType.Builder<T> builder) {
        return REGISTRAR.register(name, () -> builder.build(name));
    }
}
