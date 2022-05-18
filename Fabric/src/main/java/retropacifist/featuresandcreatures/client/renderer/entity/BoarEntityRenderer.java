package retropacifist.featuresandcreatures.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import retropacifist.featuresandcreatures.client.model.BoarEntityModel;
import retropacifist.featuresandcreatures.common.entity.mount.Boar;

public final class BoarEntityRenderer extends AbstractMountRenderer<Boar> {

    public BoarEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BoarEntityModel(), 0.75F);
    }
}