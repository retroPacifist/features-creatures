package net.msrandom.featuresandcreatures.core;

import com.google.common.collect.Maps;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.msrandom.featuresandcreatures.common.advancements.JockeyTradeTrigger;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

import java.util.Map;

public class FnCTriggers {

    public static final JockeyTradeTrigger JOCKEY_TRADE = CriteriaTriggers.register(new JockeyTradeTrigger());
}

