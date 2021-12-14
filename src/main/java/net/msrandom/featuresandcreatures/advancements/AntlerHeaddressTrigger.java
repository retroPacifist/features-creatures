package net.msrandom.featuresandcreatures.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;

public class AntlerHeaddressTrigger extends AbstractCriterionTrigger<AntlerHeaddressTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(FeaturesAndCreatures.MOD_ID,"used_headdress");

    public ResourceLocation getId() {
        return ID;
    }

    public AntlerHeaddressTrigger.Instance createInstance(JsonObject object, EntityPredicate.AndPredicate predicate, ConditionArrayParser parser) {
        ItemPredicate itempredicate = ItemPredicate.fromJson(object.get("item"));
        return new AntlerHeaddressTrigger.Instance(predicate, itempredicate);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, (p_227037_1_) -> {
            return p_227037_1_.matches(stack);
        });
    }

    public static class Instance extends CriterionInstance {
        private final ItemPredicate item;

        public Instance(EntityPredicate.AndPredicate predicate, ItemPredicate itemPredicate) {
            super(AntlerHeaddressTrigger.ID, predicate);
            this.item = itemPredicate;
        }

        public static AntlerHeaddressTrigger.Instance usedHeaddress(IItemProvider itemProvider) {
            return new AntlerHeaddressTrigger.Instance(EntityPredicate.AndPredicate.ANY, ItemPredicate.Builder.item().of(itemProvider).build());
        }

        public boolean matches(ItemStack p_215121_1_) {
            return this.item.matches(p_215121_1_);
        }

        public JsonObject serializeToJson(ConditionArraySerializer serializer) {
            JsonObject jsonobject = super.serializeToJson(serializer);
            jsonobject.add("item", this.item.serializeToJson());
            return jsonobject;
        }
    }
}
