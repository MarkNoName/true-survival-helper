package net.truesurvivalhelper.bloodmoon.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

/**
 * Loops the Blood Moon ambient track for as long as the event is active, fading its volume in on
 * start and out on {@link #requestStop()} over {@link #FADE_MILLIS} - wall-clock based (not tick
 * based) so the fade timing doesn't drift with framerate/tick rate, matching this codebase's
 * existing {@code DenialOverlayState} convention for the same reason.
 *
 * Deliberately {@link SoundSource#AMBIENT}, not MUSIC: `SoundEngine#tickNonPaused` re-checks
 * `Options#getSoundSourceVolume(source)` every tick for every playing channel and force-stops it
 * the instant that category's slider is 0 - independent of canStartSilent(), and independent of
 * this instance's own volume/tick() logic entirely. Players commonly mute "Music" specifically to
 * silence vanilla's random jukebox-style background tracks while leaving "Ambient" (cave sounds,
 * etc.) audible, which would otherwise silence this the same way. Also just the more accurate
 * category for what this actually is.
 *
 * Not positional (relative + no attenuation, like vanilla's own background music) - this is
 * ambience for the event, not a sound coming from a place in the world.
 */
@Environment(EnvType.CLIENT)
public class BloodMoonMusicInstance extends AbstractTickableSoundInstance {
	private static final long FADE_MILLIS = 3000L;

	private final long startTime = System.currentTimeMillis();
	private long fadeOutStartTime = -1L;

	public BloodMoonMusicInstance(SoundEvent event) {
		super(event, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
		this.looping = true;
		this.delay = 0;
		this.volume = 0.0F;
		this.pitch = 1.0F;
		this.relative = true;
		this.attenuation = SoundInstance.Attenuation.NONE;
	}

	/** Begins the fade-out; the instance reports itself stopped once it completes. */
	public void requestStop() {
		if (fadeOutStartTime < 0) {
			fadeOutStartTime = System.currentTimeMillis();
		}
	}

	/**
	 * Without this, SoundEngine#play sees the volume-0 starting point of the fade-in and silently
	 * drops the sound before it's ever added to the engine (only logged at DEBUG: "Skipped
	 * playing sound ..., volume was zero.") - tick() never gets a chance to raise it afterward.
	 */
	@Override
	public boolean canStartSilent() {
		return true;
	}

	@Override
	public void tick() {
		if (isStopped()) {
			return;
		}
		long now = System.currentTimeMillis();
		if (fadeOutStartTime >= 0) {
			long elapsed = now - fadeOutStartTime;
			if (elapsed >= FADE_MILLIS) {
				this.volume = 0.0F;
				stop();
			} else {
				this.volume = 1.0F - elapsed / (float) FADE_MILLIS;
			}
		} else {
			long elapsed = now - startTime;
			this.volume = Math.min(1.0F, elapsed / (float) FADE_MILLIS);
		}
	}
}
