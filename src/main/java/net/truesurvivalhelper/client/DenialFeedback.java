package net.truesurvivalhelper.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.stats.Skill;

@Environment(EnvType.CLIENT)
public final class DenialFeedback {
	private DenialFeedback() {
	}

	public static void trigger(Skill skill, int level) {
		DenialOverlayState.show(skill, level);
	}
}
