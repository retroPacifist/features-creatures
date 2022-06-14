package retropacifist.featuresandcreatures.core;

import retropacifist.featuresandcreatures.common.advancements.AntlerHeaddressTrigger;
import retropacifist.featuresandcreatures.common.advancements.JockeyTradeTrigger;
import retropacifist.featuresandcreatures.mixin.access.CriteriaTriggersAccess;

public class FnCTriggers {

    public static final JockeyTradeTrigger JOCKEY_TRADE = new JockeyTradeTrigger();
    public static final AntlerHeaddressTrigger USE_ANTLER = new AntlerHeaddressTrigger();

    public static void register() {
        CriteriaTriggersAccess.fnc_register(JOCKEY_TRADE);
        CriteriaTriggersAccess.fnc_register(USE_ANTLER);
    }
}

