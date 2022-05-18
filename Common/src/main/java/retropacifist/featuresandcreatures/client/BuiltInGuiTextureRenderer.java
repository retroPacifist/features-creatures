package retropacifist.featuresandcreatures.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.ForgeModelBakery;

import java.util.HashMap;
import java.util.Map;

public class BuiltInGuiTextureRenderer {
    private static final Map<Item, ModelResourceLocation> MODELS = new HashMap<>();

    public static void register(Item item) {
        ModelResourceLocation location = new ModelResourceLocation(Registry.ITEM.getKey(item) + "_gui", "inventory");
        ForgeModelBakery.addSpecialModel(location);
        MODELS.put(item, location);
    }

    public static ModelResourceLocation getItemModel(ItemStack stack) {
        return MODELS.get(stack.getItem());
    }
}
