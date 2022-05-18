package retropacifist.featuresandcreatures.common.entity.mount;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Constant wrapper for sounds. Yes could do abstract implementations, but the classes become ugly and long.
 */
@ParametersAreNonnullByDefault
public abstract class AbstractSoundsProviderEntity extends Animal {

    protected AbstractSoundsProviderEntity(EntityType<? extends AbstractSoundsProviderEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return getSoundsProvider().ambient;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return getSoundsProvider().hurt;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return getSoundsProvider().death;
    }

    protected abstract @NotNull SoundsProvider getSoundsProvider();

    public static final class SoundsProvider {
        final SoundEvent ambient;
        final SoundEvent hurt;
        final SoundEvent death;
        final SoundEvent saddled;

        private SoundsProvider(SoundEvent ambient, SoundEvent hurt, SoundEvent death, SoundEvent saddled) {
            this.ambient = ambient;
            this.hurt = hurt;
            this.death = death;
            this.saddled = saddled;
        }

        public static SoundsProvider create(SoundEvent ambient, SoundEvent hurt, SoundEvent death, SoundEvent saddled) {
            return new SoundsProvider(ambient, hurt, death, saddled);
        }
    }
}
