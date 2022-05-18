package retropacifist.featuresandcreatures.client.renderer.entity;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Map;
import java.util.ServiceLoader;

public interface FnCEntityRendererProvider {

    FnCEntityRendererProvider INSTANCE = Util.make(() -> {
        final var loader = ServiceLoader.load(FnCEntityRendererProvider.class);
        final var providers = Lists.newArrayList(loader.iterator());
        if (providers.isEmpty()) {
            throw new RuntimeException("No FnCEntityFactory was found on the classpath!");
        } else if (providers.size() > 1) {
            throw new RuntimeException("More than one FnCEntityFactory was found on the classpath!");
        }
        return providers.get(0);
    });

    <T extends Entity, ER extends EntityRendererProvider<T>> ER getEntityRenderer(EntityType<T> entityType);

   <T extends Entity> Map<EntityType<T>, EntityRendererProvider<T>> getRenderers();
}
