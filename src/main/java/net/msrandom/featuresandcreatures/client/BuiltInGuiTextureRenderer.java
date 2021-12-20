package net.msrandom.featuresandcreatures.client;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import java.util.HashMap;
import java.util.Map;

public class BuiltInGuiTextureRenderer {
    private static final Map<Item, ModelResourceLocation> MODELS = new HashMap<>();

    public static void register(Item item) {
        if (item.getRegistryName() == null) return;
        ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName() + "_gui", "inventory");
        ModelLoader.addSpecialModel(location);
        MODELS.put(item, location);
    }

    public static ModelResourceLocation getItemModel(ItemStack stack) {
        return MODELS.get(stack.getItem());
    }
}
