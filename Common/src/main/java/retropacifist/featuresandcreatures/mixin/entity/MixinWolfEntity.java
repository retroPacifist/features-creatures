package retropacifist.featuresandcreatures.mixin.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import retropacifist.featuresandcreatures.common.entity.mount.Sabertooth;

@Mixin(Wolf.class)
public abstract class MixinWolfEntity extends TamableAnimal {

    protected MixinWolfEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void registerSabertoothAvoidanceGoal(CallbackInfo ci) {
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Sabertooth.class, 6.0F, 1.0D, 1.2D));
    }
}
