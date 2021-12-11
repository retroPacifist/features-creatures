package net.msrandom.featuresandcreatures.mixin;

import net.minecraft.entity.LivingEntity;
import net.msrandom.featuresandcreatures.entity.AgingMountEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO: move into its own extended class, because this is yucky
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "getScale", at = @At("RETURN"), cancellable = true)
    public void getScale(CallbackInfoReturnable<Float> cir) {
        if (this instanceof AgingMountEntity) {
            float scale = cir.getReturnValue();
            cir.setReturnValue(((AgingMountEntity) this).isBaby() ? scale * 1.75F : scale);
        }
    }
}
