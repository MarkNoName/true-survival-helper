package net.truesurvivalhelper;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;

public final class TshPackInfo {
	public static final String VERSION = load();
	public static final String VERSION_SUFFIX = VERSION.isEmpty() ? "" : " " + VERSION;
	public static final String BRAND = "Minecraft True Survival" + VERSION_SUFFIX;

	private TshPackInfo() {
	}

	private static String load() {
		try {
			return Files.readString(FabricLoader.getInstance().getGameDir().resolve("version.packinfo")).trim();
		} catch (IOException e) {
			return "";
		}
	}
}
