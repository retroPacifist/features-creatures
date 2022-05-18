package retropacifist.featuresandcreatures.mixin;

import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Slime.class)
public interface SlimeSizeInvoker {
    @Invoker
    void callSetSize(int size, boolean bool);
}
