package net.msrandom.featuresandcreatures.common.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

public class AntlerHeaddressTrigger extends SimpleCriterionTrigger<AntlerHeaddressTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(FeaturesAndCreatures.MOD_ID,"used_headdress");

    public ResourceLocation getId() {
        return ID;
    }

    public AntlerHeaddressTrigger.Instance createInstance(JsonObject object, EntityPredicate.Composite predicate, DeserializationContext parser) {
        ItemPredicate itempredicate = ItemPredicate.fromJson(object.get("item"));
        return new AntlerHeaddressTrigger.Instance(predicate, itempredicate);
    }

    public void trigger(ServerPlayer player, ItemStack stack) {
        this.trigger(player, (p_227037_1_) -> {
            return p_227037_1_.matches(stack);
        });
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;

        public Instance(EntityPredicate.Composite predicate, ItemPredicate itemPredicate) {
            super(AntlerHeaddressTrigger.ID, predicate);
            this.item = itemPredicate;
        }

        public static AntlerHeaddressTrigger.Instance usedHeaddress(ItemLike itemProvider) {
            return new AntlerHeaddressTrigger.Instance(EntityPredicate.Composite.ANY, ItemPredicate.Builder.item().of(itemProvider).build());
        }

        public boolean matches(ItemStack p_215121_1_) {
            return this.item.matches(p_215121_1_);
        }

        public JsonObject serializeToJson(SerializationContext serializer) {
            JsonObject jsonobject = super.serializeToJson(serializer);
            jsonobject.add("item", this.item.serializeToJson());
            return jsonobject;
        }
    }
}
