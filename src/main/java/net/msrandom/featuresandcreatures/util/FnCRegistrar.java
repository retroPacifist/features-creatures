package net.msrandom.featuresandcreatures.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

import java.util.ArrayList;
import java.util.List;

public class FnCRegistrar<T extends IForgeRegistryEntry<T>> {
    // Default to 16 initial capacity to avoid reallocation.
    private final List<T> objects = new ArrayList<>(16);
    private final IForgeRegistry<T> registry;

    public FnCRegistrar(IForgeRegistry<T> registry) {
        this.registry = registry;
    }

    public <E extends T> E add(String name, E entry) {
        objects.add(entry.setRegistryName(new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name)));
        return entry;
    }

    public void initialize() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(registry.getRegistrySuperType(), this::register);
    }

    public void register(RegistryEvent.Register<T> event) {
        objects.forEach(event.getRegistry()::register);
        objects.clear();
    }
}
