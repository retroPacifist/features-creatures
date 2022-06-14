package retropacifist.featuresandcreatures.core;

import retropacifist.featuresandcreatures.common.block.FeaturesCreaturesBlocks;
import retropacifist.featuresandcreatures.common.block.entity.FeaturesCreaturesBlockEntities;
import retropacifist.featuresandcreatures.common.item.FeaturesCreaturesItems;

public class FnCRegistry {

    public static void loadClasses() {
        FeaturesCreaturesBlocks.init();
        FeaturesCreaturesItems.init();
        FeaturesCreaturesBlockEntities.init();
        FnCEntities.init();
        FnCSounds.init();
    }
}
