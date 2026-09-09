package net.truesurvivalhelper.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.stats.Skill;

/**
 * Holds whatever the denial-requirement box is currently showing. Wall-clock based
 * (not tick based) so it's independent of framerate/tick rate.
 */
@Environment(EnvType.CLIENT)
public final class DenialOverlayState {
	private static final long DURATION_MILLIS = 3500;
	private static final long FADE_MILLIS = 400;

	private static Skill skill = null;
	private static int level = 0;
	private static long expireAtMillis = 0;

	private DenialOverlayState() {
	}

	public static void show(Skill skill, int level) {
		DenialOverlayState.skill = skill;
		DenialOverlayState.level = level;
		DenialOverlayState.expireAtMillis = System.currentTimeMillis() + DURATION_MILLIS;
	}

	public static boolean isActive() {
		return skill != null && System.currentTimeMillis() < expireAtMillis;
	}

	/** @return 1 (fully visible) down to 0 (fully faded), fading out at the very end. */
	public static float getAlpha() {
		long remaining = expireAtMillis - System.currentTimeMillis();
		if (remaining <= 0) {
			return 0f;
		}
		if (remaining < FADE_MILLIS) {
			return remaining / (float) FADE_MILLIS;
		}
		return 1f;
	}

	public static Skill getSkill() {
		return skill;
	}

	public static int getLevel() {
		return level;
	}
}
