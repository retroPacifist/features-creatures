package retropacifist.featuresandcreatures.client.model;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import retropacifist.featuresandcreatures.common.entity.mount.AbstractMountEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class AbstractMountModel<T extends AbstractMountEntity> extends AnimatedGeoModel<T> {

    @Override
    public ResourceLocation _getTextureLocation(T object) {
        return object.isSaddled() ? getSaddledTexture(object) : getRegularTexture(object);
    }

    public abstract @NotNull ResourceLocation getSaddledTexture(T t);

    public abstract @NotNull ResourceLocation getRegularTexture(T t);
}
