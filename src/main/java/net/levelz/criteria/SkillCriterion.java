package net.levelz.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SkillCriterion extends SimpleCriterionTrigger<SkillCriterion.Conditions> {
	static final ResourceLocation ID = new ResourceLocation("levelz:skill");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	protected Conditions createInstance(JsonObject jsonObject, ContextAwarePredicate lootContextPredicate, DeserializationContext advancementEntityPredicateDeserializer) {
		SkillPredicate skillPredicate = SkillPredicate.fromJson(jsonObject.get("skill_name"));
		NumberPredicate skillLevelPredicate = NumberPredicate.fromJson(jsonObject.get("skill_level"));
		return new Conditions(lootContextPredicate, skillPredicate, skillLevelPredicate);
	}

	public void trigger(ServerPlayer player, String skillName, int skillLevel) {
		this.trigger(player, conditions -> conditions.matches(player, skillName, skillLevel));
	}

	class Conditions extends AbstractCriterionTriggerInstance {
		private final SkillPredicate skillPredicate;
		private final NumberPredicate skillLevelPredicate;

		public Conditions(ContextAwarePredicate lootContextPredicate, SkillPredicate skillPredicate, NumberPredicate skillLevelPredicate) {
			super(ID, lootContextPredicate);
			this.skillPredicate = skillPredicate;
			this.skillLevelPredicate = skillLevelPredicate;
		}

		public boolean matches(ServerPlayer player, String skillName, int skillLevel) {
			return this.skillPredicate.test(skillName) && skillLevelPredicate.test(skillLevel);
		}

		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("skill_name", this.skillPredicate.toJson());
			jsonObject.add("skill_level", this.skillLevelPredicate.toJson());
			return jsonObject;
		}
	}
}
