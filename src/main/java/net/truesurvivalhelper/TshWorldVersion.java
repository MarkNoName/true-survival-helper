package net.truesurvivalhelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TshWorldVersion {
	private static final String MARKER_FILE = "tsh_version.txt";

	private TshWorldVersion() {
	}

	public static void write(Path worldRoot) {
		if (TshPackInfo.VERSION.isEmpty()) {
			return;
		}
		Path marker = worldRoot.resolve(MARKER_FILE);
		if (Files.exists(marker)) {
			return;
		}
		try {
			Files.writeString(marker, TshPackInfo.VERSION, StandardCharsets.UTF_8);
		} catch (IOException e) {
			TrueSurvivalHelper.LOGGER.warn("Failed to write world version marker", e);
		}
	}

	public static String read(Path worldRoot) {
		try {
			return Files.readString(worldRoot.resolve(MARKER_FILE), StandardCharsets.UTF_8).trim();
		} catch (IOException e) {
			return null;
		}
	}
}
