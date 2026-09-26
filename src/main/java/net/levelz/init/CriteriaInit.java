package net.levelz.init;

import net.levelz.criteria.LevelZCriterion;
import net.levelz.criteria.SkillCriterion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class CriteriaInit {
	public static final LevelZCriterion LEVEL_UP = CriteriaTriggers.register(new LevelZCriterion());
	public static final SkillCriterion SKILL_UP = CriteriaTriggers.register(new SkillCriterion());
	public static final ObjectiveCriteria LEVELZ = ObjectiveCriteria.registerCustom("levelz");

	public static void init() {
	}
}
