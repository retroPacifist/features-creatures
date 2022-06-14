package retropacifist.featuresandcreatures.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BuiltInGuiTextureRenderer {
    private static final Map<Item, ModelResourceLocation> MODELS = new HashMap<>();

    public static ModelResourceLocation register(Item item) {
        ResourceLocation resourceLocation = Registry.ITEM.getKey(item);
        ModelResourceLocation location = new ModelResourceLocation(resourceLocation.toString() + "_gui", "inventory");
        MODELS.put(item, location);
        return location;
    }

    public static ModelResourceLocation getItemModel(ItemStack stack) {
        return MODELS.get(stack.getItem());
    }
}
