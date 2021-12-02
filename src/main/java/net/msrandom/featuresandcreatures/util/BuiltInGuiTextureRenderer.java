package net.msrandom.featuresandcreatures.util;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BuiltInGuiTextureRenderer {
    private static final Map<Item, ModelResourceLocation> MODELS = new HashMap<>();

    public static void register(Item item) {
        MODELS.put(item, null);
    }

    public static ModelResourceLocation getItemModel(ItemStack stack) {
        Item item = stack.getItem();
        if (!MODELS.containsKey(item)) return null;
        if (item.getRegistryName() == null) return null; // Not registered, so we don't care.
        return MODELS.compute(item, (k, v) -> {
            if (v == null) {
                return new ModelResourceLocation(k.getRegistryName() + "_gui", "inventory");
            } else {
                return v;
            }
        });
    }
}
