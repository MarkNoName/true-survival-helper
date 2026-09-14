package net.truesurvivalhelper.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.stats.Skill;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public final class LevelZIcons {
	public static final ResourceLocation TEXTURE = new ResourceLocation("levelz", "textures/gui/icons.png");
	public static final int SHEET_SIZE = 256;
	public static final int ICON_SIZE = 16;
	private static final int ICON_V = 16;

	private LevelZIcons() {
	}

	public static int uFor(Skill skill) {
		return skill.ordinal() * ICON_SIZE;
	}

	public static int v() {
		return ICON_V;
	}
}
