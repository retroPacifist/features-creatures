package retropacifist.featuresandcreatures.client.renderer.entity;

import com.google.auto.service.AutoService;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import retropacifist.featuresandcreatures.common.entity.BrimstoneGolem;
import retropacifist.featuresandcreatures.common.entity.ShulkrenYounglingImpl;
import retropacifist.featuresandcreatures.common.entity.Spear;
import retropacifist.featuresandcreatures.core.FnCEntities;

import java.util.Map;


@AutoService(FnCEntityRendererProvider.class)
public class FnCForgeEntityRendererProviderProvider implements FnCEntityRendererProvider {

    public static final Map<EntityType<?>, EntityRendererProvider<?>> RENDERERS = new Object2ObjectOpenHashMap<>();

    @Override
    public <T extends Entity, ER extends EntityRendererProvider<T>> ER getEntityRenderer(EntityType<T> entityType) {
        return (ER) RENDERERS.get(entityType);
    }

    @Override
    public Map<EntityType<?>, EntityRendererProvider<?>> getRenderers() {
        return RENDERERS;
    }

    private static <T extends Entity, ER extends EntityRendererProvider<T>> void addEntry(EntityType<T> entityType, ER factory) {
        RENDERERS.put(entityType, factory);
    }

    static {
        addEntry(FnCEntities.JOCKEY.get(), JockeyRenderer::new);
        addEntry(FnCEntities.BOAR.get(), BoarEntityRenderer::new);
        addEntry(FnCEntities.JACKALOPE.get(), JackalopeRenderer::new);
        addEntry(FnCEntities.SABERTOOTH.get(), SabertoothRenderer::new);
        addEntry(FnCEntities.SPEAR.get(), Spear::new);
        addEntry(FnCEntities.BLACK_FOREST_SPIRIT.get(), BlackForestSpiritRenderer::new);
        addEntry(FnCEntities.GUP.get(), GupRenderer::new);
        addEntry(FnCEntities.BRIMSTONE_GOLEM.get(), BrimstoneGolem::new);
        addEntry(FnCEntities.SHULKREN_YOUNGLING.get(), ShulkrenYounglingImpl::new);
        addEntry(FnCEntities.BFS_ATTACK.get(), BFSAttackRenderer::new);
        addEntry(FnCEntities.TBH.get(), TbhRenderer::new);
    }
}
