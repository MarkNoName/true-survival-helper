package net.truesurvivalhelper.bloodmoon.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

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

	public void requestStop() {
		if (fadeOutStartTime < 0) {
			fadeOutStartTime = System.currentTimeMillis();
		}
	}

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
