package net.truesurvivalhelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import net.fabricmc.loader.api.FabricLoader;

public final class GuiBackportChunkFadeConfig {
	private static final double DEFAULT_SECONDS = 0.75;

	private GuiBackportChunkFadeConfig() {
	}

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("tsh-chunkfade.txt");
	}

	public static double load() {
		try {
			String text = Files.readString(file(), StandardCharsets.UTF_8).trim();
			double value = Double.parseDouble(text);
			return Double.isFinite(value) ? Math.min(2.0, Math.max(0.0, value)) : DEFAULT_SECONDS;
		} catch (IOException | NumberFormatException e) {
			return DEFAULT_SECONDS;
		}
	}

	public static void save(double seconds) {
		try {
			Files.createDirectories(file().getParent());
			Files.writeString(file(), String.format(Locale.ROOT, "%.2f", seconds), StandardCharsets.UTF_8);
		} catch (IOException e) {
		}
	}
}
