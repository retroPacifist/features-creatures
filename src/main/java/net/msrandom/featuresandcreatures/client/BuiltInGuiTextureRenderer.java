package net.msrandom.featuresandcreatures.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.ForgeModelBakery;

import java.util.HashMap;
import java.util.Map;

public class BuiltInGuiTextureRenderer {
    private static final Map<Item, ModelResourceLocation> MODELS = new HashMap<>();

    public static void register(Item item) {
        if (item.getRegistryName() == null) return;
        ForgeModelBakery.addSpecialModel(new ResourceLocation(item.getRegistryName().getNamespace(), "item/" + item.getRegistryName().getPath() + "_in_hand"));
        MODELS.put(item, null);
    }

    public static ModelResourceLocation getItemModel(ItemStack stack) {
        Item item = stack.getItem();
        if (item.getRegistryName() == null) return null; // Not registered, so we don't care.
        if (!MODELS.containsKey(item)) return null;
        return MODELS.compute(item, (k, v) -> {
            if (v == null) {
                return new ModelResourceLocation(k.getRegistryName(), "inventory");
            } else {
                return v;
            }
        });
    }
}
