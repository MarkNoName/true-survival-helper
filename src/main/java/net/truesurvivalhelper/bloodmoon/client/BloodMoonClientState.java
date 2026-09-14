package net.truesurvivalhelper.bloodmoon.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.truesurvivalhelper.bloodmoon.BloodMoonNetworking;

@Environment(EnvType.CLIENT)
public final class BloodMoonClientState {
	private static final long FADE_DURATION_MILLIS = 4500L;

	private static boolean active = false;
	private static long lastToggleTime = 0L;
	private static float blendAtToggle = 0.0F;
	private static BloodMoonMusicInstance music = null;

	public static final float MOON_TINT = 0.25F;

	public static final float FOG_RED = 0.35F;
	public static final float FOG_GREEN = 0.02F;
	public static final float FOG_BLUE = 0.04F;

	public static final float SKY_RED = 0.09F;
	public static final float SKY_GREEN = 0.01F;
	public static final float SKY_BLUE = 0.015F;

	public static final float CLOUD_RED = 0.2F;
	public static final float CLOUD_GREEN = 0.02F;
	public static final float CLOUD_BLUE = 0.03F;

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
