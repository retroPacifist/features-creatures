package net.msrandom.featuresandcreatures.entity.mount;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRideable;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.msrandom.featuresandcreatures.core.FnCEntities;
import net.msrandom.featuresandcreatures.core.FnCSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.msrandom.featuresandcreatures.FeaturesAndCreatures.createEntity;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class Boar extends AbstractAngryMountEntity implements IRideable {
    private static final SoundsProvider SOUNDS_PROVIDER = SoundsProvider.create(
            FnCSounds.BOAR_AMBIENT,
            FnCSounds.BOAR_HURT,
            FnCSounds.BOAR_DEATH,
            FnCSounds.BOAR_SADDLE);
    private static final Ingredient FOODS = Ingredient.of(Items.CARROT);
    private static final String WALK_ANIMATION = "animation.boar.walk";
    private static final String ATTACK_ANIMATION = "animation.boar.walk";

    public Boar(EntityType<? extends Boar> entityType, World world) {
        super(entityType, world);
    }

    public static @NotNull AttributeModifierMap.MutableAttribute createBoarAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 11.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0F);
    }

    @Override
    protected void registerAdditionalGoals() {
        goalSelector.addGoal(3, new TemptGoal(this, 1.2D, Ingredient.of(Items.CARROT_ON_A_STICK), false));
        goalSelector.addGoal(2, new PanicGoal(this, 1.42D));
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity playerEntity, Hand hand) {
        if (!playerEntity.isCrouching() && !isFood(playerEntity.getItemInHand(hand)) && isSaddled() && !isVehicle() && !playerEntity.isSecondaryUseActive()) {
            return sidedOperation(() -> playerEntity.startRiding(this));
        }
        return super.mobInteract(playerEntity, hand);
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
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        playSound(FnCSounds.BOAR_STEP, 0.15F, 1.0F);
    }

    @Override
    public @Nullable AgeableEntity getBreedOffspring(ServerWorld serverWorld, AgeableEntity entity) {
        return createEntity(FnCEntities.BOAR, serverWorld, boarEntity -> boarEntity.setAge(-24000));
    }

    @Override
    protected double getBreedWalkSpeed() {
        return 0.8D;
    }

    // IRideable
    @Override
    public boolean boost() {
        return false;
    }

    @Override
    public void travelWithInput(Vector3d p_230267_1_) {
    }

    @Override
    public float getSteeringSpeed() {
        return 0;
    }
}
