package net.msrandom.featuresandcreatures.entity.mount;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createEntity;

public final class Sabertooth extends AbstractAngryMountEntity {
    private static final SoundsProvider SOUNDS_PROVIDER = SoundsProvider.create(
            FnCSounds.SABERTOOTH_AMBIENT,
            FnCSounds.SABERTOOTH_HURT,
            FnCSounds.SABERTOOTH_DEATH,
            FnCSounds.SABERTOOTH_SADDLE);
    private static final Ingredient FOODS = Ingredient.of(Items.SALMON, Items.COD, Items.MUTTON);
    private static final String WALK_ANIMATION = "animation.sabertooth.walk";
    private static final String ATTACK_ANIMATION = "animation.sabertooth.attack";

    public Sabertooth(EntityType<? extends Sabertooth> entityType, World world) {
        super(entityType, world);
    }

    public static @NotNull AttributeModifierMap.MutableAttribute createSabertoothAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 4.0F);
    }

    @Override
    protected void registerAdditionalGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, SheepEntity.class, 10, true, true, null));
    }

    @Override
    public @NotNull Ingredient getFoods() {
        return FOODS;
    }

    @Override
    protected @NotNull String getWalkAnimation() {
        return WALK_ANIMATION;
    }

    @Override
    protected @NotNull String getAttackAnimation() {
        return ATTACK_ANIMATION;
    }

    @Override
    protected @NotNull SoundsProvider getSoundsProvider() {
        return SOUNDS_PROVIDER;
    }

    @Override
    public @Nullable AgeableEntity getBreedOffspring(ServerWorld serverWorld, AgeableEntity entity) {
        return createEntity(FnCEntities.SABERTOOTH, serverWorld, boarEntity -> boarEntity.setAge(-24000));
    }
}
