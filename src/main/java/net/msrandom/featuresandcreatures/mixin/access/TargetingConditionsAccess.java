package net.msrandom.featuresandcreatures.mixin.access;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TargetingConditions.class)
public interface TargetingConditionsAccess {

   @Invoker("<init>")
    static TargetingConditions create (boolean bool){
       throw new Error("Mixin did not apply!");
   }
}
