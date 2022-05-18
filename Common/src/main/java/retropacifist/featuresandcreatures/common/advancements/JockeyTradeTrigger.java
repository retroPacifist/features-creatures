package retropacifist.featuresandcreatures.common.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import retropacifist.featuresandcreatures.FeaturesAndCreatures;
import retropacifist.featuresandcreatures.common.entity.Jockey;

public class JockeyTradeTrigger extends SimpleCriterionTrigger<JockeyTradeTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(FeaturesAndCreatures.MOD_ID, "jockey_trade");

    public ResourceLocation getId() {
        return ID;
    }

    public Instance createInstance(JsonObject object, EntityPredicate.Composite entity, DeserializationContext parser) {
        EntityPredicate.Composite entitypredicate$andpredicate = EntityPredicate.Composite.fromJson(object, "jockey", parser);
        ItemPredicate itempredicate = ItemPredicate.fromJson(object.get("item"));
        return new Instance(entity, entitypredicate$andpredicate, itempredicate);
    }

    public void trigger(ServerPlayer player, Jockey jockey, ItemStack item) {
        LootContext lootcontext = EntityPredicate.createContext(player, jockey);
        this.trigger(player, instance -> instance.matches(lootcontext, item));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite jockey;
        private final ItemPredicate item;

        public Instance(EntityPredicate.Composite andPredicate, EntityPredicate.Composite jockey, ItemPredicate item) {
            super(JockeyTradeTrigger.ID, andPredicate);
            this.jockey = jockey;
            this.item = item;
        }

        public static Instance tradedWithJockey() {
            return new Instance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, ItemPredicate.ANY);
        }

        public boolean matches(LootContext p_236575_1_, ItemStack p_236575_2_) {
            if (!this.jockey.matches(p_236575_1_)) {
                return false;
            } else {
                return this.item.matches(p_236575_2_);
            }
        }

        public JsonObject serializeToJson(SerializationContext p_230240_1_) {
            JsonObject jsonobject = super.serializeToJson(p_230240_1_);
            jsonobject.add("item", this.item.serializeToJson());
            jsonobject.add("jockey", this.jockey.toJson(p_230240_1_));
            return jsonobject;
        }
    }
}