package net.levelz.stats;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.network.PlayerStatsServerPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.truesurvivalhelper.LevelZSkillAccess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlayerStatsManager {
	private final Player playerEntity;

	public int overallLevel;
	private int totalLevelExperience;
	public float levelProgress;
	private int skillPoints;
	private final Map<Skill, Integer> skillLevel = new HashMap<>();

	public PlayerStatsManager(Player playerEntity) {
		this.playerEntity = playerEntity;
	}

	public Player getPlayerEntity() {
		return playerEntity;
	}

	public List<Integer> lockedBlockIds = new ArrayList<Integer>();
	public List<Integer> lockedbrewingItemIds = new ArrayList<Integer>();
	public List<Integer> lockedSmithingItemIds = new ArrayList<Integer>();
	public List<Integer> lockedCraftingItemIds = new ArrayList<Integer>();

	public void readNbt(CompoundTag tag) {
		if (tag.contains("SkillPoints", 99)) {
			this.overallLevel = tag.getInt("Level");
			this.levelProgress = tag.getFloat("LevelProgress");
			this.totalLevelExperience = tag.getInt("TotalLevelExperience");
			this.skillPoints = tag.getInt("SkillPoints");
			for (Skill stats : Skill.values()) {
				skillLevel.put(stats, tag.getInt(stats.getNbt()));
			}
		}
	}

	public void writeNbt(CompoundTag tag) {
		tag.putInt("Level", this.overallLevel);
		tag.putFloat("LevelProgress", this.levelProgress);
		tag.putInt("TotalLevelExperience", this.totalLevelExperience);
		tag.putInt("SkillPoints", this.skillPoints);
		skillLevel.forEach((k, v) -> tag.putInt(k.getNbt(), v));
	}

	public void setOverallLevel(int overallLevel) {
		this.overallLevel = overallLevel;
	}

	public int getOverallLevel() {
		return overallLevel;
	}

	public void setTotalLevelExperience(int totalLevelExperience) {
		this.totalLevelExperience = totalLevelExperience;
	}

	public int getTotalLevelExperience() {
		return totalLevelExperience;
	}

	public void setSkillPoints(int skillPoints) {
		this.skillPoints = skillPoints;
	}

	public int getSkillPoints() {
		return skillPoints;
	}

	public void setLevelProgress(float levelProgress) {
		this.levelProgress = ConfigInit.CONFIG.useIndependentExp ? levelProgress : 0;
	}

	public float getLevelProgress() {
		if (!ConfigInit.CONFIG.useIndependentExp) {
			return Math.min(getNonIndependentExperience() / (float) this.getNextLevelExperience(), 1F);
		}
		return levelProgress;
	}

	public void setSkillLevel(Skill skill, int level) {
		skillLevel.put(skill, level);
	}

	public int getSkillLevel(Skill skill) {
		if (skillLevel.containsKey(skill)) {
			return skillLevel.get(skill);
		}
		return 0;
	}

	@Deprecated
	public void setLevel(String string, int level) {
		switch (string) {
			case "level" -> this.overallLevel = level;
			case "points" -> this.skillPoints = level;
			default -> setSkillLevel(Skill.valueOf(string.toUpperCase()), level);
		}
	}

	@Deprecated
	public int getLevel(String string) {
		return switch (string) {
			case "level" -> this.overallLevel;
			case "points" -> this.skillPoints;
			default -> getSkillLevel(Skill.valueOf(string.toUpperCase()));
		};
	}

	public void addExperienceLevels(int levels) {
		this.overallLevel += levels;
		this.skillPoints += ConfigInit.CONFIG.pointsPerLevel;
		if (this.overallLevel < 0) {
			this.overallLevel = 0;
			this.levelProgress = 0.0F;
			this.totalLevelExperience = 0;
		}
	}

	public boolean isMaxLevel() {
		if (ConfigInit.CONFIG.overallMaxLevel != 0) {
			return this.overallLevel >= ConfigInit.CONFIG.overallMaxLevel;
		}
		return this.overallLevel >= ConfigInit.CONFIG.maxLevel * 12;
	}

	public boolean hasAvailableLevel() {
		return this.skillPoints > 0;
	}

	public int getNextLevelExperience() {
		if (isMaxLevel()) {
			return 0;
		}
		int experienceCost = (int) (ConfigInit.CONFIG.xpBaseCost + ConfigInit.CONFIG.xpCostMultiplicator * Math.pow(this.overallLevel, ConfigInit.CONFIG.xpExponent));
		if (ConfigInit.CONFIG.xpMaxCost != 0)
			return experienceCost >= ConfigInit.CONFIG.xpMaxCost ? ConfigInit.CONFIG.xpMaxCost : experienceCost;
		else
			return experienceCost;
	}

	private int lastExperienceLevel = -1;
	private float lastExperienceProgress = -1;
	private long lastExperience = -1;

	public long getNonIndependentExperience() {
		int level = playerEntity.experienceLevel;
		float experienceProgress = playerEntity.experienceProgress;
		if (level == lastExperienceLevel && experienceProgress == lastExperienceProgress) {
			return lastExperience;
		}
		long exp = 0;
		for (int i = 0; i < level; i++) {
			if (i >= 30) {
				exp += 112 + (i - 30) * 9;
			} else {
				exp += i >= 15 ? 37 + (i - 15) * 5 : 7 + i * 2;
			}
		}
		exp = (long) (exp + playerEntity.getXpNeededForNextLevel() * experienceProgress);
		lastExperienceLevel = level;
		lastExperienceProgress = experienceProgress;
		lastExperience = exp;
		return exp;
	}

	public static boolean playerLevelisHighEnough(Player playerEntity, List<Object> list, String string, boolean creativeRequired) {
		if (!playerEntity.isCreative() || !creativeRequired) {
			PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager();
			int playerLevel = 0;
			int maxLevel = ConfigInit.CONFIG.maxLevel;
			if (string != null) {
				if (!list.isEmpty() && list.contains(string)) {
					playerLevel = playerStatsManager.getLevel(list.get(list.indexOf(string) + 1).toString());
					if (playerLevel < maxLevel) {
						if (playerLevel < (int) list.get(list.indexOf(string) + 2))
							return false;
					}
				}
			} else {
				if (!list.isEmpty()) {
					playerLevel = playerStatsManager.getLevel(list.get(0).toString());
					if (playerLevel < maxLevel && playerLevel < (int) list.get(1))
						return false;
				}
			}
		}

		return true;
	}

	public static boolean weaponLevelisHighEnough(Player playerEntity) {
		Item item = playerEntity.getMainHandItem().getItem();
		if (!(item instanceof SwordItem) && !(item instanceof AxeItem)) {
			return true;
		}
		String material = ((TieredItem) item).getTier().toString().toLowerCase(Locale.ROOT);
		ArrayList<Object> levelList = item instanceof AxeItem && !ConfigInit.CONFIG.bindAxeDamageToSwordRestriction ? LevelLists.axeList : LevelLists.swordList;
		if (playerLevelisHighEnough(playerEntity, levelList, material, true)) {
			return true;
		}
		int index = levelList.indexOf(material);
		if (index < 0) {
			return true;
		}
		if (!playerEntity.level().isClientSide()) {
			Skill skill = Skill.valueOf(levelList.get(index + 1).toString().toUpperCase(Locale.ROOT));
			LevelZSkillAccess.sendDenialMessage(playerEntity, skill, Integer.parseInt(levelList.get(index + 2).toString()));
		}
		return false;
	}

	public static boolean listContainsItemOrBlock(Player playerEntity, int id, int reference) {
		PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager();
		if (reference == 1) {
			if (playerStatsManager.getSkillLevel(Skill.MINING) < ConfigInit.CONFIG.maxLevel && playerStatsManager.lockedBlockIds.contains(id))
				return true;
		} else if (reference == 2) {
			if (playerStatsManager.getSkillLevel(Skill.ALCHEMY) < ConfigInit.CONFIG.maxLevel && playerStatsManager.lockedbrewingItemIds.contains(id))
				return true;
		} else if (reference == 3) {
			if (playerStatsManager.getSkillLevel(Skill.SMITHING) < ConfigInit.CONFIG.maxLevel && playerStatsManager.lockedSmithingItemIds.contains(id))
				return true;
		} else if (reference == 4) {
			if (playerStatsManager.lockedCraftingItemIds.contains(id))
				return true;
		}
		return false;
	}

	public static int getUnlockLevel(int id, int reference) {
		if (reference == 1) {
			for (int i = 0; i < LevelLists.miningBlockList.size(); i++) {
				if (LevelLists.miningBlockList.get(i).contains(id)) {
					return LevelLists.miningLevelList.get(i);
				}
			}
			return 0;
		} else if (reference == 2) {
			for (int i = 0; i < LevelLists.brewingItemList.size(); i++) {
				if (LevelLists.brewingItemList.get(i).contains(id)) {
					return LevelLists.brewingLevelList.get(i);
				}
			}
			return 0;
		} else if (reference == 3) {
			for (int i = 0; i < LevelLists.smithingItemList.size(); i++) {
				if (LevelLists.smithingItemList.get(i).contains(id)) {
					return LevelLists.smithingLevelList.get(i);
				}
			}
			return 0;
		} else if (reference == 4) {
			for (int i = 0; i < LevelLists.craftingItemList.size(); i++) {
				if (LevelLists.craftingItemList.get(i).contains(id)) {
					return LevelLists.craftingLevelList.get(i);
				}
			}
			return 0;
		}
		return 0;
	}

	public boolean resetSkill(Skill skill) {
		int sLevel = this.getSkillLevel(skill);
		if (sLevel > 0) {
			this.setSkillPoints(this.getSkillPoints() + sLevel);
			this.setSkillLevel(skill, 0);
			PlayerStatsServerPacket.writeS2CResetSkillPacket((ServerPlayer) playerEntity, skill);
			return true;
		} else
			return false;
	}

	public static boolean resetSkill(Player playerEntity, Skill skill) {
		PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager();
		return playerStatsManager.resetSkill(skill);
	}

	public static void onLevelUp(Player playerEntity, int playerLevel) {
	}
}
