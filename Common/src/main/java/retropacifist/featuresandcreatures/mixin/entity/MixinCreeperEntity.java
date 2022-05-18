package retropacifist.featuresandcreatures.mixin.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import retropacifist.featuresandcreatures.common.entity.mount.Sabertooth;

@Mixin(Creeper.class)
public abstract class MixinCreeperEntity extends Monster {

    protected MixinCreeperEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void registerSabertoothAvoidanceGoal(CallbackInfo ci) {
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Sabertooth.class, 6.0F, 1.0D, 1.2D));
    }
}
