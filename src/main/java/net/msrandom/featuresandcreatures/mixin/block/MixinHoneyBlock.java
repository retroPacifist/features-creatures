package net.msrandom.featuresandcreatures.mixin.block;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.HoneyBlock;
import net.msrandom.featuresandcreatures.common.entity.FloatingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneyBlock.class)
public class MixinHoneyBlock {

    @Inject(method = "doesEntityDoHoneyBlockSlideEffects", at = @At("HEAD"), cancellable = true)
    private static void stepOnExceptions(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity le) {
            if (le instanceof FloatingEntity) {
                cir.setReturnValue(false);
            }
        }
    }
}