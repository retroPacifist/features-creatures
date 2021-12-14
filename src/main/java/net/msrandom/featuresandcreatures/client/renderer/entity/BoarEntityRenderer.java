package net.msrandom.featuresandcreatures.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.msrandom.featuresandcreatures.client.model.BoarEntityModel;
import net.msrandom.featuresandcreatures.entity.mount.BoarEntity;

public final class BoarEntityRenderer extends AbstractMountRenderer<BoarEntity> {

    public BoarEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BoarEntityModel(), 0.75F);
    }
}