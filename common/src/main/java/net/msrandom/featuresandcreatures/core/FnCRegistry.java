package net.msrandom.featuresandcreatures.core;

import net.msrandom.featuresandcreatures.common.block.FeaturesCreaturesBlocks;
import net.msrandom.featuresandcreatures.common.block.entity.FeaturesCreaturesBlockEntities;
import net.msrandom.featuresandcreatures.common.item.FeaturesCreaturesItems;

public class FnCRegistry {

    public static void loadClasses() {
        FeaturesCreaturesBlocks.init();
        FeaturesCreaturesItems.init();
        FeaturesCreaturesBlockEntities.init();
        FnCEntities.init();
        FnCSounds.init();
    }
}
