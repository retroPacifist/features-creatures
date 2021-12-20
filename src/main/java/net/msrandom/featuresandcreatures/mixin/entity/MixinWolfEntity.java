package net.msrandom.featuresandcreatures.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.world.World;
import net.msrandom.featuresandcreatures.entity.mount.Sabertooth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public abstract class MixinWolfEntity extends TameableEntity {

    protected MixinWolfEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void registerSabertoothAvoidanceGoal(CallbackInfo ci) {
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Sabertooth.class, 6.0F, 1.0D, 1.2D));
    }
}
