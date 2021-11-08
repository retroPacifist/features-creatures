package net.msrandom.featuresandcreatures.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.common.entities.boar.Boar;
import net.msrandom.featuresandcreatures.common.entities.jackalope.Jackalope;
import net.msrandom.featuresandcreatures.common.entities.jockey.Jockey;
import net.msrandom.featuresandcreatures.common.entities.sabertooth.Sabertooth;

import java.util.ArrayList;
import java.util.List;

public class FnAEntities {

    public static List<EntityType<?>> entities = new ArrayList<>();

    public static final EntityType<Jockey> JOCKEY = createEntity("jockey", EntityType.Builder.of(Jockey::new, EntityClassification.CREATURE).sized(0.2f, 0.3f).build("jockey"));
    public static final EntityType<Boar> BOAR = createEntity("boar", EntityType.Builder.of(Boar::new, EntityClassification.CREATURE).sized(0.9F, 0.9F).build("boar"));
    public static final EntityType<Jackalope> JACKALOPE = createEntity("jackalope", EntityType.Builder.of(Jackalope::new, EntityClassification.CREATURE).sized(1.2F, 1.3F).build("jackalope"));
    public static final EntityType<Sabertooth> SABERTOOTH = createEntity("sabertooth", EntityType.Builder.of(Sabertooth::new, EntityClassification.CREATURE).sized(1.2F, 1.3F).build("sabertooth"));


    public static <E extends Entity, ET extends EntityType<E>> ET createEntity(String id, ET entityType) {
        entityType.setRegistryName(new ResourceLocation(FeaturesAndCreatures.MOD_ID, id));
        entities.add(entityType);
        return entityType;
    }

    public static void init(){
    }
}
