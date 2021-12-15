package net.msrandom.featuresandcreatures.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class FnCConfig {
    private static final ForgeConfigSpec CONFIG_SPEC;
    private static final FnCConfig INSTANCE;

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> jockeyEffects;

    static {
        final Pair<FnCConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(FnCConfig::new);
        CONFIG_SPEC = specPair.getRight();
        INSTANCE = specPair.getLeft();
    }

    public FnCConfig(ForgeConfigSpec.Builder builder) {
        jockeyEffects = builder.defineListAllowEmpty(Collections.singletonList("jockeyEffectBlacklist"), Collections::emptyList, effect -> effect instanceof String && ForgeRegistries.ENTITIES.containsKey(new ResourceLocation((String) effect)));
    }

    public Set<Potion> getJockeyEffectBlacklist() {
        return jockeyEffects.get().stream()
                .map(ResourceLocation::new)
                .filter(ForgeRegistries.POTIONS::containsKey)
                .map(ForgeRegistries.POTIONS::getValue)
                .collect(Collectors.toSet());
    }

    public static ForgeConfigSpec getConfigSpec() {
        return CONFIG_SPEC;
    }

    public static FnCConfig getInstance() {
        return INSTANCE;
    }
}
