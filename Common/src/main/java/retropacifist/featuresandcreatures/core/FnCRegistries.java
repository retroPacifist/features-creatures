package retropacifist.featuresandcreatures.core;

import retropacifist.featuresandcreatures.common.block.FeaturesCreaturesBlocks;
import retropacifist.featuresandcreatures.common.block.entity.FeaturesCreaturesBlockEntities;
import retropacifist.featuresandcreatures.common.item.FeaturesCreaturesItems;

public class FnCRegistries {

    public static void bootStrap() {
        FeaturesCreaturesBlocks.bootStrap();
        FeaturesCreaturesBlockEntities.bootStrap();
        FeaturesCreaturesItems.bootStrap();
    }
}
