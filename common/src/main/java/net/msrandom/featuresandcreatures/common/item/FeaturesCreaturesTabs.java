package net.msrandom.featuresandcreatures.common.item;

import lombok.experimental.UtilityClass;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class FeaturesCreaturesTabs {
    public final CreativeModeTab GENERAL = new CreativeModeTab(FeaturesAndCreatures.MOD_ID) {

        @Override
        public @NotNull ItemStack makeIcon() {
            return FeaturesCreaturesItems.MEGA_POTION.get().getDefaultInstance();
        }
    };
}
