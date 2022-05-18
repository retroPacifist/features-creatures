package retropacifist.featuresandcreatures.common.entity;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ServiceLoader;

public interface FnCEntityFactoryProvider {

    FnCEntityFactoryProvider INSTANCE = Util.make(() -> {
        final var loader = ServiceLoader.load(FnCEntityFactoryProvider.class);
        final var providers = Lists.newArrayList(loader.iterator());
        if (providers.isEmpty()) {
            throw new RuntimeException("No FnCEntityFactory was found on the classpath!");
        } else if (providers.size() > 1) {
            throw new RuntimeException("More than one FnCEntityFactory was found on the classpath!");
        }
        return providers.get(0);
    });

    <T extends Entity, FACTORY extends EntityType.EntityFactory<T>> FACTORY getEntityFactory(Class<T> tClass);
}
