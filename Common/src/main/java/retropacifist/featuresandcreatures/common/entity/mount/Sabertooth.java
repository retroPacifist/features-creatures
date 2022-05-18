package retropacifist.featuresandcreatures.common.entity.mount;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retropacifist.featuresandcreatures.core.FnCEntities;
import retropacifist.featuresandcreatures.core.FnCSounds;

import static retropacifist.featuresandcreatures.FeaturesAndCreatures.createEntity;

public final class Sabertooth extends AbstractAngryMountEntity {
    private static final SoundsProvider SOUNDS_PROVIDER = SoundsProvider.create(
            FnCSounds.SABERTOOTH_AMBIENT.get(),
            FnCSounds.SABERTOOTH_HURT.get(),
            FnCSounds.SABERTOOTH_DEATH.get(),
            FnCSounds.SABERTOOTH_SADDLE.get());
    private static final Ingredient FOODS = Ingredient.of(Items.SALMON, Items.COD, Items.MUTTON);
    private static final String WALK_ANIMATION = "animation.sabertooth.walk";
    private static final String ATTACK_ANIMATION = "animation.sabertooth.attack";

    public Sabertooth(EntityType<? extends Sabertooth> entityType, Level world) {
        super(entityType, world);
    }

    public static @NotNull AttributeSupplier.Builder createSabertoothAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 4.0F);
    }

    @Override
    protected void registerAdditionalGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Sheep.class, 10, true, true, null));
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
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob entity) {
        return createEntity(FnCEntities.SABERTOOTH.get(), serverWorld, sabertooth -> sabertooth.setAge(-24000));
    }
}
