package net.msrandom.featuresandcreatures.mixin;

import net.minecraft.entity.monster.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SlimeEntity.class)
public interface SlimeSizeInvoker {
    @Invoker
    void callSetSize(int size, boolean bool);
}
