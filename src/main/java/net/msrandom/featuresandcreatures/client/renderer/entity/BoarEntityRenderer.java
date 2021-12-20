package net.msrandom.featuresandcreatures.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.msrandom.featuresandcreatures.client.model.BoarEntityModel;
import net.msrandom.featuresandcreatures.entity.mount.Boar;

public final class BoarEntityRenderer extends AbstractMountRenderer<Boar> {

    public BoarEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BoarEntityModel(), 0.75F);
    }
}