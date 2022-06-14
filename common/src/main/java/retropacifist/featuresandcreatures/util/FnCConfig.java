package retropacifist.featuresandcreatures.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.platform.ModPlatform;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record FnCConfig(List<MobEffect> jockeyEffects, boolean namedJockeyDespawn, double jockeySpawnChance) {

    public static FnCConfig INSTANCE = null;
    public static FnCConfig DEFAULT = new FnCConfig(new ArrayList<>(), false, 0.5);


    public static final Codec<FnCConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                            Registry.MOB_EFFECT.byNameCodec().listOf().fieldOf("jockeyEffects").orElse(new ArrayList<>()).forGetter(FnCConfig::jockeyEffects),
                            Codec.BOOL.fieldOf("namedJockeyDespawn").forGetter(FnCConfig::namedJockeyDespawn),
                            Codec.doubleRange(0, 1).fieldOf("jockeySpawnChance").forGetter(FnCConfig::jockeySpawnChance)
                    )
                    .apply(builder, FnCConfig::new));

    public static FnCConfig getConfig() {
        return getConfig(false, false);
    }

    public static FnCConfig getConfig(boolean serialize, boolean recreate) {
        if (INSTANCE == null || serialize || recreate) {
            INSTANCE = readConfig(recreate);
        }

        return INSTANCE;
    }

    private static FnCConfig readConfig(boolean recreate) {
        final Path path = ModPlatform.INSTANCE.configPath().resolve(FeaturesAndCreatures.MOD_ID + ".json");

        if (!path.toFile().exists() || recreate) {
            JsonElement jsonElement = CODEC.encodeStart(JsonOps.INSTANCE, DEFAULT).result().get();

            try {
                Files.createDirectories(path.getParent());
                Files.write(path, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(jsonElement).getBytes());
            } catch (IOException e) {
                FeaturesAndCreatures.LOGGER.error(e.toString());
            }
        }
        FeaturesAndCreatures.LOGGER.info(String.format("\"%s\" was read.", path.toString()));

        try {
            return CODEC.decode(JsonOps.INSTANCE, new JsonParser().parse(new FileReader(path.toFile()))).result().orElseThrow(RuntimeException::new).getFirst();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return DEFAULT;
    }
}
