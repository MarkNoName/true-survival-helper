package net.levelz.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class LevelZCriterion extends SimpleCriterionTrigger<LevelZCriterion.Conditions> {
	private static final ResourceLocation ID = new ResourceLocation("levelz:level");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	protected Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate lootContextPredicate, DeserializationContext advancementEntityPredicateDeserializer) {
		NumberPredicate numberPredicate = NumberPredicate.fromJson(jsonObject.get("level"));
		return new Conditions(lootContextPredicate, numberPredicate);
	}

	public void trigger(ServerPlayer player, int level) {
		this.trigger(player, conditions -> conditions.matches(player, level));
	}

	class Conditions extends AbstractCriterionTriggerInstance {
		private final NumberPredicate numberPredicate;

		public Conditions(ContextAwarePredicate lootContextPredicate, NumberPredicate numberPredicate) {
			super(ID, lootContextPredicate);
			this.numberPredicate = numberPredicate;
		}

		public boolean matches(ServerPlayer player, int level) {
			return this.numberPredicate.test(level);
		}

		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("level", this.numberPredicate.toJson());
			return jsonObject;
		}
	}
}
