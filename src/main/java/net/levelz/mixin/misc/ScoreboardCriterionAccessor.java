package net.levelz.mixin.misc;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ObjectiveCriteria.class)
public interface ScoreboardCriterionAccessor {
	@Invoker("registerCustom")
	static ObjectiveCriteria callCreate(String name) {
		throw new AssertionError("Untransformed Accessor!");
	}
}
