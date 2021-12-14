package net.msrandom.featuresandcreatures.core;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = FeaturesAndCreatures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FnCSounds {

    public static final List<SoundEvent> SOUNDS = new ArrayList<>();

    public static SoundEvent BOAR_HURT = createSound("entity.boar.hurt");
    public static SoundEvent BOAR_AMBIENT = createSound("entity.boar.ambient");
    public static SoundEvent BOAR_DIE = createSound("entity.boar.die");
    public static SoundEvent BOAR_ATTACK = createSound("entity.boar.attack");
    public static SoundEvent BOAR_SADDLE = createSound("entity.boar.saddle");
    public static SoundEvent BOAR_STEP = createSound("entity.boar.step");
    public static SoundEvent BOAR_DESADDLE = createSound("entity.boar.desaddle");

    public static SoundEvent JOCKEY_HURT = createSound("entity.jockey.hurt");
    public static SoundEvent JOCKEY_AMBIENT = createSound("entity.jockey.ambient");
    public static SoundEvent JOCKEY_DIE = createSound("entity.jockey.die");
    public static SoundEvent JOCKEY_ATTACK = createSound("entity.jockey.attack");
    public static SoundEvent JOCKEY_TRADE = createSound("entity.jockey.trade");

    public static SoundEvent JACKALOPE_HURT = createSound("entity.jackalope.hurt");
    public static SoundEvent JACKALOPE_AMBIENT = createSound("entity.jackalope.ambient");
    public static SoundEvent JACKALOPE_DIE = createSound("entity.jackalope.die");
    public static SoundEvent JACKALOPE_SADDLE = createSound("entity.jackalope.saddle");
    public static SoundEvent JACKALOPE_STEP = createSound("entity.jackalope.step");
    public static SoundEvent JACKALOPE_DESADDLE = createSound("entity.jackalope.desaddle");

    public static SoundEvent SABERTOOTH_HURT = createSound("entity.sabertooth.hurt");
    public static SoundEvent SABERTOOTH_ATTACK = createSound("entity.sabertooth.attack");
    public static SoundEvent SABERTOOTH_SADDLE = createSound("entity.sabertooth.saddle");

    public static SoundEvent SPEAR_THROW = createSound("entity.spear.throw");
    public static SoundEvent SPEAR_ATTACK = createSound("entity.spear.attack");
    public static SoundEvent SPEAR_MISS = createSound("entity.spear.miss");

    public static SoundEvent ANTLER_HEADDRESS_ATTACK_STRONG = createSound("entity.antler_headdress.attack_strong");
    public static SoundEvent ANTLER_HEADDRESS_CHARGE = createSound("entity.antler_headdress.charge");
    public static SoundEvent ANTLER_HEADDRESS_FINISH_CHARGE = createSound("entity.antler_headdress.finish_charge");


    public static SoundEvent createSound(String location) {
        ResourceLocation soundLocation = new ResourceLocation(FeaturesAndCreatures.MOD_ID, location);
        SoundEvent soundEvent = new SoundEvent(soundLocation);
        soundEvent.setRegistryName(soundLocation);
        SOUNDS.add(soundEvent);
        return soundEvent;
    }


    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        FeaturesAndCreatures.LOGGER.debug("FnC: Registering sounds...");
        SOUNDS.forEach(soundEvent -> event.getRegistry().register(soundEvent));
        FeaturesAndCreatures.LOGGER.info("FnC: Sounds registered!");
    }
}
