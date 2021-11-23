package net.msrandom.featuresandcreatures.core;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class FnCKeybinds {
    public static KeyBinding CHARGE_ANTLER = new KeyBinding("Charge Antler", GLFW.GLFW_KEY_RIGHT_SHIFT, "Features And Creatures");

    public static void register(){
        ClientRegistry.registerKeyBinding(CHARGE_ANTLER);
    }
}
