package retropacifist.featuresandcreatures.core;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.entity.*;
import retropacifist.featuresandcreatures.common.entity.mount.Boar;
import retropacifist.featuresandcreatures.common.entity.mount.Jackalope;
import retropacifist.featuresandcreatures.common.entity.mount.Sabertooth;
import retropacifist.featuresandcreatures.mixin.access.SpawnPlacementsAccess;
import retropacifist.reg.RegistrationProvider;
import retropacifist.reg.RegistryObject;

public class FnCEntities {
    public static final RegistrationProvider<EntityType<?>> PROVIDER = RegistrationProvider.get(Registry.ENTITY_TYPE, FeaturesAndCreatures.MOD_ID);
    private static final FnCEntityFactoryProvider ENTITY_FACTORY = FnCEntityFactoryProvider.INSTANCE;

    public static final RegistryObject<EntityType<Jockey>> JOCKEY = createEntity("jockey", EntityType.Builder.of(ENTITY_FACTORY.getEntityFactory(Jockey.class), MobCategory.CREATURE).sized(0.25f, 1f));
    public static final RegistryObject<EntityType<Boar>> BOAR = createEntity("boar", EntityType.Builder.of(ENTITY_FACTORY.getEntityFactory(Boar.class), MobCategory.CREATURE).sized(0.9F, 0.9F));
    public static final RegistryObject<EntityType<Jackalope>> JACKALOPE = createEntity("jackalope", EntityType.Builder.of(ENTITY_FACTORY.getEntityFactory(Jackalope.class), MobCategory.CREATURE).sized(1F, 0.8F));
    public static final RegistryObject<EntityType<Sabertooth>> SABERTOOTH = createEntity("sabertooth", EntityType.Builder.of(ENTITY_FACTORY.getEntityFactory(Sabertooth.class), MobCategory.CREATURE).sized(1.2F, 1.3F));
    public static final RegistryObject<EntityType<BlackForestSpirit>> BLACK_FOREST_SPIRIT = createEntity("black_forest_spirit", EntityType.Builder.of(ENTITY_FACTORY.getEntityFactory(BlackForestSpirit.class), MobCategory.CREATURE).sized(0.7F, 2f));
    public static final RegistryObject<EntityType<Gup>> GUP = createEntity("gup", EntityType.Builder.of(ENTITY_FACTORY.getEntityFactory(Gup.class), MobCategory.CREATURE).sized(2.625F, 2f));
    public static final RegistryObject<EntityType<BrimstoneGolem>> BRIMSTONE_GOLEM = createEntity("brimstone_golem", EntityType.Builder.of(ENTITY_FACTORY.getEntityFactory(BrimstoneGolem.class), MobCategory.CREATURE).sized(2.6F, 4f));
    public static final RegistryObject<EntityType<ShulkrenYoungling>> SHULKREN_YOUNGLING = createEntity("shulkren_youngling", EntityType.Builder.of(ENTITY_FACTORY.getEntityFactory(ShulkrenYoungling.class), MobCategory.CREATURE).sized(0.8F, 1.65f));
    public static final RegistryObject<EntityType<BFSAttack>> BFS_ATTACK = createEntity("bfs_attack", EntityType.Builder.<BFSAttack>of(ENTITY_FACTORY.getEntityFactory(BFSAttack.class), MobCategory.MISC).sized(0.4F, 0.4f));
    public static final RegistryObject<EntityType<Tbh>> TBH = createEntity("tbh", EntityType.Builder.of(ENTITY_FACTORY.getEntityFactory(Tbh.class), MobCategory.CREATURE).sized(0.4F, 0.6f));

    public static final RegistryObject<EntityType<Spear>> SPEAR = createEntity("spear", EntityType.Builder.<Spear>of(ENTITY_FACTORY.getEntityFactory(Spear.class), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));

    private static <T extends Entity> RegistryObject<EntityType<T>> createEntity(String name, EntityType.Builder<T> builder) {
        return PROVIDER.register(name, () -> builder.build(name));
    }

    public static void registerSpawnPlacements() {
        SpawnPlacementsAccess.fnc_invokeRegister(FnCEntities.BLACK_FOREST_SPIRIT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlackForestSpirit::checkSpawnRules);
        SpawnPlacementsAccess.fnc_invokeRegister(FnCEntities.SHULKREN_YOUNGLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ShulkrenYoungling::checkSpawnRules);
        SpawnPlacementsAccess.fnc_invokeRegister(FnCEntities.BRIMSTONE_GOLEM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BrimstoneGolem::checkSpawnRules);
        SpawnPlacementsAccess.fnc_invokeRegister(FnCEntities.GUP.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Gup::checkSpawnRules);
        SpawnPlacementsAccess.fnc_invokeRegister(FnCEntities.TBH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Tbh::checkSpawnRules);
    }

    public static void bootStrap() {
        registerSpawnPlacements();
    }
}
