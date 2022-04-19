package net.msrandom.featuresandcreatures.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.msrandom.featuresandcreatures.entity.FloatingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BasePressurePlateBlock.class)
public abstract class MixinBasePressurePlateBlock extends Block {

    @Shadow
    protected abstract BlockState setSignalForState(BlockState p_49301_, int p_49302_);

    public MixinBasePressurePlateBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Inject(method = "checkPressed", at = @At("HEAD"), cancellable = true)
    public void entityInside(Entity entity, Level p_152145_, BlockPos p_152146_, BlockState p_152147_, int p_152148_, CallbackInfo ci) {
        if (entity instanceof LivingEntity le) {
            if (le instanceof FloatingEntity) {
                ci.cancel();
            }
        }
    }
}
