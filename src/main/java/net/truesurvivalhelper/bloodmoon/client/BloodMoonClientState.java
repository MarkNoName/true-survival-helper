package net.truesurvivalhelper.bloodmoon.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.truesurvivalhelper.bloodmoon.BloodMoonNetworking;

/**
 * Client-side mirror of the server's Blood Moon active flag, kept in sync via
 * {@link BloodMoonNetworking#CHANNEL}. Also holds the shared visual constants the render mixins
 * use, so fog/sky/moon can't drift out of sync with each other.
 *
 * Activation/deactivation fades over {@link #FADE_DURATION_MILLIS} rather than snapping instantly
 * - {@link #getBlend()} is what every render mixin actually reads (0 = untouched vanilla/Polytone
 * look, 1 = full Blood Moon), interpolating each mixin's own vanilla-computed value toward its
 * Blood Moon target rather than hard-switching between the two.
 */
@Environment(EnvType.CLIENT)
public final class BloodMoonClientState {
	/** 3-6s felt right for "great" per the user's own ask; landed in the middle of that range. */
	private static final long FADE_DURATION_MILLIS = 4500L;

	private static boolean active = false;
	/** Wall-clock time (not tick-based) the last activate/deactivate happened, for the fade. */
	private static long lastToggleTime = 0L;
	/** The blend value at the moment of that last toggle, so reversing mid-fade doesn't jump. */
	private static float blendAtToggle = 0.0F;
	/** The currently-playing (or fading out) ambient track, if any. */
	private static BloodMoonMusicInstance music = null;

	/** Moon: multiplies the existing green/blue shader-color channels, keeping vanilla's own alpha/rain-fade. */
	public static final float MOON_TINT = 0.25F;

	// Ground-level fog - saturated enough to read clearly as red up close, confirmed working for
	// both the background (clearColor) and actual terrain (setShaderFogColor).
	public static final float FOG_RED = 0.35F;
	public static final float FOG_GREEN = 0.02F;
	public static final float FOG_BLUE = 0.04F;

	// Sky disc + the getSkyColor call FogRenderer itself blends from - deliberately much darker
	// than fog: a flat 0.35 read as an overpoweringly red sky that drowned out the moon and
	// didn't look like a real night sky at all. This keeps the sky mostly black with just a red
	// cast, so the (unrelated, separately-tinted) moon still stands out against it.
	public static final float SKY_RED = 0.09F;
	public static final float SKY_GREEN = 0.01F;
	public static final float SKY_BLUE = 0.015F;

	// Clouds - a bit brighter than the sky behind them so they stay a visible, distinguishable
	// shape (matching how clouds normally read a bit lighter than the sky itself) instead of
	// disappearing into a same-shade background, without being as saturated as ground fog.
	public static final float CLOUD_RED = 0.2F;
	public static final float CLOUD_GREEN = 0.02F;
	public static final float CLOUD_BLUE = 0.03F;

	// Very close start (right at the player) - 160 for FOG_END read as "barely there" up close
	// (at a linear fade over 4-160, even 40 blocks out is only ~23% fogged), so this pulls the
	// whole fade band back in tight so the effect is actually felt near the player, not just far away.
	//
	// FOG_END deliberately never goes below 80: Sodium's "fog occlusion" performance option
	// couples its chunk-search distance directly to RenderSystem.getShaderFogEnd()
	// (RenderSectionManager#getEffectiveRenderDistance) - forcing this lower previously made
	// Sodium stop rendering chunk sections past that point entirely, which looked exactly like
	// "chunks not loading" in testing. 80 is the tightest confirmed-safe value. If it should feel
	// even closer than this, that now needs Sodium's own "Fog Occlusion" performance option
	// turned off client-side (in Sodium's video settings) - past this point it's fighting that
	// setting, not this mod.
	public static final float FOG_START = 4.0F;
	public static final float FOG_END = 80.0F;

	private BloodMoonClientState() {
	}

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(BloodMoonNetworking.CHANNEL, (client, listener, buf, responseSender) -> {
			boolean value = buf.readBoolean();
			client.execute(() -> setActive(value));
		});
	}

	private static void setActive(boolean value) {
		if (value == active) {
			return;
		}
		blendAtToggle = getBlend();
		lastToggleTime = System.currentTimeMillis();
		active = value;

		if (value) {
			music = new BloodMoonMusicInstance(BloodMoonSounds.BLOOD_MOON_MUSIC);
			Minecraft.getInstance().getSoundManager().play(music);
		} else if (music != null) {
			music.requestStop();
			music = null;
		}
	}

	public static boolean isActive() {
		return active;
	}

	/** 0 = fully normal, 1 = full Blood Moon - every render mixin lerps its own vanilla value toward its target by this. */
	public static float getBlend() {
		float target = active ? 1.0F : 0.0F;
		long elapsed = System.currentTimeMillis() - lastToggleTime;
		if (elapsed >= FADE_DURATION_MILLIS) {
			return target;
		}
		float t = elapsed / (float) FADE_DURATION_MILLIS;
		return Mth.lerp(t, blendAtToggle, target);
	}
}
