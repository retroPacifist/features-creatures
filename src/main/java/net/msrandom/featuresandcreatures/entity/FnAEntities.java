package net.msrandom.featuresandcreatures.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

public class FnAEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRAR = DeferredRegister.create(ForgeRegistries.ENTITIES, FeaturesAndCreatures.MOD_ID);
    public static final RegistryObject<EntityType<JockeyEntity>> JOCKEY = create("jockey", EntityType.Builder.of(JockeyEntity::new, EntityClassification.CREATURE).sized(0.2f, 0.3f));

    private static <T extends Entity> RegistryObject<EntityType<T>> create(String name, EntityType.Builder<T> builder) {
        EntityType<T> type = builder.build(name);
        // TODO make spawn egg based on type and name
        return REGISTRAR.register(name, () -> type);
    }
}
