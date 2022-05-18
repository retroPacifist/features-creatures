package retropacifist.featuresandcreatures.common.item;

import lombok.experimental.UtilityClass;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;

@UtilityClass
public class FeaturesCreaturesTabs {
    public final CreativeModeTab GENERAL = new CreativeModeTab(FeaturesAndCreatures.MOD_ID) {

        @Override
        public @NotNull ItemStack makeIcon() {
            return FeaturesCreaturesItems.MEGA_POTION.get().getDefaultInstance();
        }
    };
}
