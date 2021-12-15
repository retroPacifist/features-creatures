package net.msrandom.featuresandcreatures.core;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class FnCKeybinds {
    public static KeyMapping CHARGE_ANTLER = new KeyMapping("Charge Antler", GLFW.GLFW_KEY_RIGHT_SHIFT, "Features And Creatures");

    public static void register(){
        ClientRegistry.registerKeyBinding(CHARGE_ANTLER);
    }
}
