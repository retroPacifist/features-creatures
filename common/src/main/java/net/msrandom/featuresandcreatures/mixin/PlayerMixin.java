package net.msrandom.featuresandcreatures.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.msrandom.featuresandcreatures.network.IChargeHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IChargeHandler {

    public CompoundTag data;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public CompoundTag saveWithoutId(CompoundTag compound) {
        if (this.data != null) {
            compound.put("ModData", this.data.copy());
        }
        if (compound.contains("ModData", 10)) {
            this.data = compound.getCompound("ModData");
        }
        return super.saveWithoutId(compound);
    }

    @Override
    public CompoundTag getPersistentData() {
        if (data == null) {
            data = new CompoundTag();
        }
        return data;
    }
}
