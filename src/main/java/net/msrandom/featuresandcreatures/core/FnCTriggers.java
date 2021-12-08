package net.msrandom.featuresandcreatures.core;

import net.minecraft.advancements.CriteriaTriggers;
import net.msrandom.featuresandcreatures.advancements.JockeyTradeTrigger;

public class FnCTriggers {

    public static final JockeyTradeTrigger JOCKEY_TRADE = new JockeyTradeTrigger();

    public static void register(){
       CriteriaTriggers.register(JOCKEY_TRADE);
    }
}

