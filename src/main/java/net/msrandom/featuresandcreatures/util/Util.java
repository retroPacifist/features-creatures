package net.msrandom.featuresandcreatures.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class Util {

    @Nullable
    public static BlockPos fromCompound(@Nullable CompoundNBT nbt) {
        if (nbt == null) {
            return null;
        }
        return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    public static CompoundNBT posToCompound(BlockPos pos) {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("x", pos.getX());
        compoundNBT.putInt("y", pos.getY());
        compoundNBT.putInt("z", pos.getZ());
        return compoundNBT;
    }
}
