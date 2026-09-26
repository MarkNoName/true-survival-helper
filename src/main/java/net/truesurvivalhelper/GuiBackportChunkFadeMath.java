package net.truesurvivalhelper;

public final class GuiBackportChunkFadeMath {
	public static final double CLOSE_DISTANCE_SQR = 768.0;

	private GuiBackportChunkFadeMath() {
	}

	public static float computeVisibility(long elapsedMs, long durationMs, double distanceSqr) {
		if (durationMs <= 0L || distanceSqr < CLOSE_DISTANCE_SQR) {
			return 1.0F;
		}

		return elapsedMs >= durationMs ? 1.0F : (float) elapsedMs / (float) durationMs;
	}
}
