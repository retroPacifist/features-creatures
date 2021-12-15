package net.msrandom.featuresandcreatures.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.Level;
import net.msrandom.featuresandcreatures.entity.Sabertooth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class MixinCreeperEntity extends MonsterEntity {

    protected MixinCreeperEntity(EntityType<? extends MonsterEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void registerSabertoothAvoidanceGoal(CallbackInfo ci) {
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Sabertooth.class, 6.0F, 1.0D, 1.2D));
    }
}
