package retropacifist.featuresandcreatures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FeaturesAndCreatures {
    public static final String MOD_ID = "featuresandcreatures";
    public static final Logger LOGGER = LogManager.getLogger();

    public FeaturesAndCreatures() {

    }

    public static <T extends Entity> @Nullable T createEntity(EntityType<T> entityType, Level world, Consumer<T> consumer) {
        T entity = entityType.create(world);
        if (entity != null) {
            consumer.accept(entity);
        }
        return entity;
    }

    public static ResourceLocation createResourceLocation(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
