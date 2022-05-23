package net.msrandom.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.common.entity.mount.AbstractMountEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class AbstractMountModel<T extends AbstractMountEntity> extends AnimatedGeoModel<T> {

    @Override
    public ResourceLocation _getTextureLocation(T object) {
        return object.isSaddled() ? getSaddledTexture(object) : getRegularTexture(object);
    }

    public abstract @NotNull ResourceLocation getSaddledTexture(T t);

    public abstract @NotNull ResourceLocation getRegularTexture(T t);
}
