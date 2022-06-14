package retropacifist.featuresandcreatures.core;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class FnCKeybinds {
    public static final KeyMapping CHARGE_ANTLER = new KeyMapping("Charge Antler", GLFW.GLFW_KEY_RIGHT_SHIFT, "Features And Creatures");

    public static List<KeyMapping> register() {
        return List.of(CHARGE_ANTLER);
    }
}
