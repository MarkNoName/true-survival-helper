package net.truesurvivalhelper;

import java.util.Locale;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.stats.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class LevelZSkillAccess {
	private LevelZSkillAccess() {
	}

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

	public static boolean meetsLevelForTooltip(Player player, Skill skill, int requiredLevel) {
		if (requiredLevel <= 0) {
			return true;
		}
		if (!(player instanceof PlayerStatsManagerAccess access)) {
			return true;
		}
		return access.getPlayerStatsManager().getSkillLevel(skill) >= requiredLevel;
	}

	public static void sendDenialMessage(Player player, Skill skill, int requiredLevel) {
		String key = "item.levelz." + skill.toString().toLowerCase(Locale.ROOT) + ".tooltip";
		player.displayClientMessage(Component.translatable(key, requiredLevel).withStyle(ChatFormatting.RED), true);
	}
}
