package net.msrandom.featuresandcreatures.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.msrandom.featuresandcreatures.entity.FloatingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaBlock.class)
public class MixinMagmaBlock {

    @Inject(method = "stepOn", at = @At("HEAD"))
    public void stepOnExceptions(Level p_153777_, BlockPos p_153778_, BlockState p_153779_, Entity entity, CallbackInfo ci){
        if (entity instanceof LivingEntity le){
            if (le instanceof FloatingEntity) {
                le.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1, 1, false, false));
            }
        }
    }
}