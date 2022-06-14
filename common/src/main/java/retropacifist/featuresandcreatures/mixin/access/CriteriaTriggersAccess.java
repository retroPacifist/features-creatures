package retropacifist.featuresandcreatures.mixin.access;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CriteriaTriggers.class)
public interface CriteriaTriggersAccess {

    @Invoker("register")
    static <T extends CriterionTrigger<?>> T fnc_register(T criterion) {
        throw new Error("Mixin did not apply");
    }
}
