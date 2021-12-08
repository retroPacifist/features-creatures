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
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;
import net.msrandom.featuresandcreatures.FeaturesAndCreatures;
import net.msrandom.featuresandcreatures.entity.Jockey;

public class JockeyTradeTrigger extends AbstractCriterionTrigger<JockeyTradeTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "jockey_trade");

    public ResourceLocation getId() {
        return ID;
    }

    public JockeyTradeTrigger.Instance createInstance(JsonObject object, EntityPredicate.AndPredicate entity, ConditionArrayParser parser) {
        EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(object, "jockey", parser);
        ItemPredicate itempredicate = ItemPredicate.fromJson(object.get("item"));
        return new JockeyTradeTrigger.Instance(entity, entitypredicate$andpredicate, itempredicate);
    }

    public void trigger(ServerPlayerEntity player, Jockey jockey, ItemStack item) {
        LootContext lootcontext = EntityPredicate.createContext(player, jockey);
        this.trigger(player, (p_227267_2_) -> {
            return p_227267_2_.matches(lootcontext, item);
        });
    }

    public static class Instance extends CriterionInstance {
        private final EntityPredicate.AndPredicate jockey;
        private final ItemPredicate item;

        public Instance(EntityPredicate.AndPredicate andPredicate, EntityPredicate.AndPredicate jockey, ItemPredicate item) {
            super(JockeyTradeTrigger.ID, andPredicate);
            this.jockey = jockey;
            this.item = item;
        }

        public static JockeyTradeTrigger.Instance tradedWithJockey() {
            return new JockeyTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(LootContext p_236575_1_, ItemStack p_236575_2_) {
            if (!this.jockey.matches(p_236575_1_)) {
                return false;
            } else {
                return this.item.matches(p_236575_2_);
            }
        }

        public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
            JsonObject jsonobject = super.serializeToJson(p_230240_1_);
            jsonobject.add("item", this.item.serializeToJson());
            jsonobject.add("jockey", this.jockey.toJson(p_230240_1_));
            return jsonobject;
        }
    }
}