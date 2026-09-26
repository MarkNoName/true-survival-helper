package net.truesurvivalhelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class TshDefaultOptions {
	private static final Map<String, String> VALUES = load();

	private TshDefaultOptions() {
	}

	public static String get(String key) {
		return VALUES.get(key);
	}

	private static Map<String, String> load() {
		Map<String, String> values = new HashMap<>();
		try (InputStream in = TshDefaultOptions.class.getResourceAsStream("/defaultoptions.txt")) {
			if (in == null) {
				TrueSurvivalHelper.LOGGER.warn("Bundled defaultoptions.txt resource not found on the classpath");
				return values;
			}
			for (String line : new String(in.readAllBytes(), StandardCharsets.UTF_8).lines().toList()) {
				int split = line.indexOf(':');
				if (split < 0) {
					continue;
				}
				values.put(line.substring(0, split), line.substring(split + 1));
			}
		} catch (IOException e) {
			TrueSurvivalHelper.LOGGER.warn("Failed to read bundled default options", e);
		}
		return values;
	}
}
