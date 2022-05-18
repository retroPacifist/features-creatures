package retropacifist.featuresandcreatures.platform;

import com.google.auto.service.AutoService;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import retropacifist.featuresandcreatures.common.entity.*;
import retropacifist.featuresandcreatures.common.entity.mount.Boar;
import retropacifist.featuresandcreatures.common.entity.mount.Jackalope;
import retropacifist.featuresandcreatures.common.entity.mount.Sabertooth;

import java.util.Map;

@AutoService(FnCEntityFactoryProvider.class)
public class FnCForgeEntityFactoryProvider implements FnCEntityFactoryProvider {
    public static final Map<Class<?>, EntityType.EntityFactory<?>> FACTORIES = new Object2ObjectOpenHashMap<>();

    @Override
    public <T extends Entity, FACTORY extends EntityType.EntityFactory<T>> FACTORY getEntityFactory(Class<T> tClass) {
        return (FACTORY) FACTORIES.get(tClass);
    }

    private static <T extends Entity, FACTORY extends EntityType.EntityFactory<? extends T>> void addEntry(Class<? extends T> clazz, FACTORY factory) {
        FACTORIES.put(clazz, factory);
    }

    static {
        addEntry(Jockey.class, Jockey::new);
        addEntry(Boar.class, Boar::new);
        addEntry(Jackalope.class, Jackalope::new);
        addEntry(Sabertooth.class, Sabertooth::new);
        addEntry(BlackForestSpiritImpl.class, BlackForestSpiritImpl::new);
        addEntry(Gup.class, Gup::new);
        addEntry(BrimstoneGolem.class, BrimstoneGolemImpl::new);
        addEntry(ShulkrenYounglingImpl.class, ShulkrenYounglingImpl::new);
        addEntry(BFSAttack.class, (type, level) -> new BFSAttack(level));
        addEntry(TbhImpl.class, TbhImpl::new);
        addEntry(JockeyImpl.class, JockeyImpl::new);
    }
}
