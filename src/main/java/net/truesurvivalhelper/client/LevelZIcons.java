package net.truesurvivalhelper.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.stats.Skill;
import net.minecraft.resources.ResourceLocation;

/**
 * LevelZ's own per-skill icon sprites - referenced directly from LevelZ's own texture
 * (never copied into this mod's resources: LevelZ is already a hard dependency, and
 * this is its art, not ours) rather than substituting arbitrary vanilla items.
 *
 * Confirmed from LevelZ's own SkillScreen source (net.levelz.screen.SkillScreen#init):
 * the 12 skill-selector tab buttons use a 16x16 region per skill, all on one row at
 * v=16, u = Skill.values()[i] index * 16 - i.e. u = skill.ordinal() * 16. Same
 * ordering as the Skill enum (HEALTH, STRENGTH, AGILITY, DEFENSE, STAMINA, LUCK,
 * ARCHERY, TRADE, SMITHING, MINING, FARMING, ALCHEMY).
 */
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
