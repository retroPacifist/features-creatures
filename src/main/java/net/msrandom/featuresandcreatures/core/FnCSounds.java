package net.msrandom.featuresandcreatures.core;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.util.FnCRegistrar;

@Mod.EventBusSubscriber(modid = FeaturesAndCreatures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FnCSounds {
    public static final FnCRegistrar<SoundEvent> REGISTRAR = new FnCRegistrar<>(ForgeRegistries.SOUND_EVENTS);

    public static SoundEvent BOAR_HURT = createSound("entity.boar.hurt");
    public static SoundEvent BOAR_AMBIENT = createSound("entity.boar.ambient");
    public static SoundEvent BOAR_DEATH = createSound("entity.boar.death");
    public static SoundEvent BOAR_ATTACK = createSound("entity.boar.attack");
    public static SoundEvent BOAR_SADDLE = createSound("entity.boar.saddle");
    public static SoundEvent BOAR_STEP = createSound("entity.boar.step");

    public static SoundEvent JOCKEY_HURT = createSound("entity.jockey.hurt");
    public static SoundEvent JOCKEY_DEATH = createSound("entity.jockey.death");
    public static SoundEvent JOCKEY_ATTACK = createSound("entity.jockey.attack");

    public static SoundEvent JACKALOPE_HURT = createSound("entity.jackalope.hurt");
    public static SoundEvent JACKALOPE_AMBIENT = createSound("entity.jackalope.ambient");
    public static SoundEvent JACKALOPE_DEATH = createSound("entity.jackalope.death");
    public static SoundEvent JACKALOPE_SADDLE = createSound("entity.jackalope.saddle");
    public static SoundEvent JACKALOPE_STEP = createSound("entity.jackalope.step");

    public static SoundEvent SABERTOOTH_AMBIENT = createSound("entity.sabertooth.ambient");
    public static SoundEvent SABERTOOTH_HURT = createSound("entity.sabertooth.hurt");
    public static SoundEvent SABERTOOTH_ATTACK = createSound("entity.sabertooth.attack");
    public static SoundEvent SABERTOOTH_SADDLE = createSound("entity.sabertooth.saddle");
    public static SoundEvent SABERTOOTH_DEATH = createSound("entity.sabertooth.death");

    public static SoundEvent SPEAR_THROW = createSound("entity.spear.throw");
    public static SoundEvent SPEAR_ATTACK = createSound("entity.spear.attack");
    public static SoundEvent SPEAR_MISS = createSound("entity.spear.miss");

    public static SoundEvent ANTLER_HEADDRESS_ATTACK_STRONG = createSound("entity.antler_headdress.attack_strong");
    public static SoundEvent ANTLER_HEADDRESS_CHARGE = createSound("entity.antler_headdress.charge");
    public static SoundEvent ANTLER_HEADDRESS_FINISH_CHARGE = createSound("entity.antler_headdress.finish_charge");

    public static SoundEvent ENTITY_DESADDLE = createSound("entity.saddled.desaddle");

    public static SoundEvent DOWSING_ROD_LOCATES = createSound("item.dowsing_rod.locates");

    public static SoundEvent createSound(String location) {
        return REGISTRAR.add(
                location.replace('.', '_'),
                new SoundEvent(new ResourceLocation(FeaturesAndCreatures.MOD_ID, location))
        );
    }
}
