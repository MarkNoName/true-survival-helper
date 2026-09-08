package net.truesurvivalhelper;

import java.util.Locale;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.stats.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Reads a player's real, live LevelZ skill level - the exact same data LevelZ's own
 * Mixins read (net.levelz.stats.PlayerStatsManager, via the access widener interface
 * it already exposes) - so restrictions added by this mod never desync from LevelZ.
 */
public final class LevelZSkillAccess {
	private LevelZSkillAccess() {
	}

	/**
	 * @return true if the given player meets the required LevelZ skill level, or the
	 * requirement is 0 (unrestricted). Creative-mode players always pass, mirroring
	 * LevelZ's own {@code creativeRequired = true} behaviour used by all of its
	 * built-in restrictions.
	 */
	public static boolean meetsLevel(Player player, Skill skill, int requiredLevel) {
		if (requiredLevel <= 0) {
			return true;
		}
		if (player.isCreative()) {
			return true;
		}
		if (!(player instanceof PlayerStatsManagerAccess access)) {
			return true;
		}
		return access.getPlayerStatsManager().getSkillLevel(skill) >= requiredLevel;
	}

	/**
	 * Shows the same red action-bar denial message LevelZ's own restrictions show
	 * (reuses LevelZ's own "item.levelz.&lt;skill&gt;.tooltip" translation keys), so a
	 * block from this mod looks and reads exactly like a native LevelZ restriction.
	 */
	public static void sendDenialMessage(Player player, Skill skill, int requiredLevel) {
		String key = "item.levelz." + skill.toString().toLowerCase(Locale.ROOT) + ".tooltip";
		player.displayClientMessage(Component.translatable(key, requiredLevel).withStyle(ChatFormatting.RED), true);
	}
}
