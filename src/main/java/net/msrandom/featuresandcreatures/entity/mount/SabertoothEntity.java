package net.msrandom.featuresandcreatures.entity.mount;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public final class SabertoothEntity extends AbstractAngryMountEntity {

    public SabertoothEntity(EntityType<? extends SabertoothEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerAdditionalGoals() {

    }

    @Override
    public @NotNull Ingredient getFoods() {
        return null;
    }

    @Override
    protected <T extends IAnimatable> PlayState getPlayState(AnimationEvent<T> event) {
        return null;
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }
}
