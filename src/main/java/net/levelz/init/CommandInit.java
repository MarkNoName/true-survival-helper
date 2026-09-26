package net.levelz.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import org.apache.commons.lang3.StringUtils;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CommandInit {
	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
			dispatcher.register((Commands.literal("info").requires((serverCommandSource) -> {
				return serverCommandSource.hasPermission(3);
			})).then(Commands.literal("material").executes((commandContext) -> {
				return executeInfoMaterial(commandContext.getSource());
			})));
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
			dispatcher.register((Commands.literal("playerstats").requires((serverCommandSource) -> {
				return serverCommandSource.hasPermission(2);
			})).then(Commands.argument("targets", EntityArgument.players())
					.then(Commands.literal("add").then(Commands.literal("level").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "level",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("points").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "points",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("health").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "health",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("strength").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "strength",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("agility").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "agility",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("defense").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "defense",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("stamina").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "stamina",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("luck").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "luck",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("archery").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "archery",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("trade").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "trade",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("smithing").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "smithing",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("mining").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "mining",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("farming").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "farming",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("alchemy").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "alchemy",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))).then(Commands.literal("experience").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "experience",
								IntegerArgumentType.getInteger(commandContext, "level"), 0);
					}))))
					.then(Commands.literal("remove").then(Commands.literal("level").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "level",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("points").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "points",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("health").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "health",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("strength").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "strength",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("agility").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "agility",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("defense").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "defense",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("stamina").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "stamina",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("luck").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "luck",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("archery").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "archery",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("trade").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "trade",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("smithing").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "smithing",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("mining").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "mining",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("farming").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "farming",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("alchemy").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "alchemy",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))).then(Commands.literal("experience").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "experience",
								IntegerArgumentType.getInteger(commandContext, "level"), 1);
					}))))
					.then(Commands.literal("set").then(Commands.literal("level").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "level",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("points").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "points",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("health").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "health",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("strength").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "strength",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("agility").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "agility",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("defense").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "defense",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("stamina").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "stamina",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("luck").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "luck",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("archery").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "archery",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("trade").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "trade",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("smithing").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "smithing",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("mining").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "mining",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("farming").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "farming",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("alchemy").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "alchemy",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))).then(Commands.literal("experience").then(Commands.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "experience",
								IntegerArgumentType.getInteger(commandContext, "level"), 2);
					}))))
					.then(Commands.literal("get").then(Commands.literal("level").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "level", 0, 3);
					})).then(Commands.literal("all").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "all", 0, 3);
					})).then(Commands.literal("points").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "points", 0, 3);
					})).then(Commands.literal("health").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "health", 0, 3);
					})).then(Commands.literal("strength").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "strength", 0, 3);
					})).then(Commands.literal("agility").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "agility", 0, 3);
					})).then(Commands.literal("defense").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "defense", 0, 3);
					})).then(Commands.literal("stamina").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "stamina", 0, 3);
					})).then(Commands.literal("luck").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "luck", 0, 3);
					})).then(Commands.literal("archery").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "archery", 0, 3);
					})).then(Commands.literal("trade").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "trade", 0, 3);
					})).then(Commands.literal("smithing").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "smithing", 0, 3);
					})).then(Commands.literal("mining").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "mining", 0, 3);
					})).then(Commands.literal("farming").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "farming", 0, 3);
					})).then(Commands.literal("alchemy").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "alchemy", 0, 3);
					})).then(Commands.literal("experience").executes((commandContext) -> {
						return executeSkillCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), "experience", 0, 3);
					})))));
		});
	}

	private static int executeSkillCommand(CommandSourceStack source, Collection<ServerPlayer> targets, String skill, int i, int reference) {
		Iterator<ServerPlayer> playerIterator = targets.iterator();

		i = Mth.abs(i);
		while (playerIterator.hasNext()) {
			ServerPlayer serverPlayerEntity = playerIterator.next();
			PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager();
			if (skill.equals("experience")) {
				if (reference == 0)
					((PlayerSyncAccess) serverPlayerEntity).addLevelExperience(i);
				if (reference == 1) {
					int currentXP = (int) (playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience());
					float oldProgress = playerStatsManager.getLevelProgress();
					playerStatsManager.setLevelProgress(currentXP - i > 0 ? (float) (currentXP - 1) / (float) playerStatsManager.getNextLevelExperience() : 0.0F);
					playerStatsManager.setTotalLevelExperience(currentXP - i > 0 ? playerStatsManager.getTotalLevelExperience() - i
							: playerStatsManager.getTotalLevelExperience() - (int) (oldProgress * playerStatsManager.getNextLevelExperience()));
				}
				if (reference == 2) {
					float oldProgress = playerStatsManager.getLevelProgress();
					playerStatsManager.setLevelProgress(i >= playerStatsManager.getNextLevelExperience() ? 1.0F : (float) i / playerStatsManager.getNextLevelExperience());
					playerStatsManager.setTotalLevelExperience((int) (playerStatsManager.getTotalLevelExperience() - oldProgress * playerStatsManager.getNextLevelExperience()
							+ playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience()));
				}
				if (reference == 3) {
					source.sendSuccess(() -> Component.translatable("commands.playerstats.printProgress", serverPlayerEntity.getDisplayName(),
							(int) (playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience()), playerStatsManager.getNextLevelExperience()), true);
				}
			} else {
				int playerSkillLevel;
				if (skill.equals("points")) {
					playerSkillLevel = playerStatsManager.getSkillPoints();
				} else if (skill.equals("level")) {
					playerSkillLevel = playerStatsManager.getOverallLevel();
				} else {
					playerSkillLevel = playerStatsManager.getSkillLevel(Skill.valueOf(skill.toUpperCase()));
				}
				if (reference == 0) {
					playerSkillLevel += i;
				}
				if (reference == 1) {
					playerSkillLevel = playerSkillLevel - i > 0 ? playerSkillLevel - i : 0;
				}
				if (reference == 2) {
					playerSkillLevel = i;
				}
				if (reference == 3) {
					if (skill.equals("all")) {
						for (int u = 0; u < skillStrings().size(); u++) {
							skill = skillStrings().get(u);
							if (skill.equals("experience")) {
								source.sendSuccess(() -> Component.translatable("commands.playerstats.printProgress", serverPlayerEntity.getDisplayName(),
										(int) (playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience()), playerStatsManager.getNextLevelExperience()), true);
							} else {
								final String finalSkill = skillStrings().get(u);
								source.sendSuccess(() -> Component.translatable("commands.playerstats.printLevel", serverPlayerEntity.getDisplayName(),
										StringUtils.capitalize(finalSkill) + (finalSkill.equals("level") || finalSkill.equals("points") ? ":" : " Level:"),
										finalSkill.equals("level") ? playerStatsManager.getOverallLevel()
												: finalSkill.equals("points") ? playerStatsManager.getSkillPoints() : playerStatsManager.getSkillLevel(Skill.valueOf(finalSkill.toUpperCase()))),
										true);
							}
						}
					} else {
						final String finalSkill = skill;
						final int finalPlayerSkillLevel = playerSkillLevel;
						source.sendSuccess(() -> Component.translatable("commands.playerstats.printLevel", serverPlayerEntity.getDisplayName(),
								StringUtils.capitalize(finalSkill) + (finalSkill.equals("level") || finalSkill.equals("points") ? ":" : " Level:"), finalPlayerSkillLevel), true);
					}
					continue;
				}
				if (skill.equals("points")) {
					playerStatsManager.setSkillPoints(playerSkillLevel);
				} else if (skill.equals("level")) {
					playerStatsManager.setOverallLevel(playerSkillLevel);
					final int level = playerSkillLevel;
					serverPlayerEntity.getScoreboard().forAllObjectives(CriteriaInit.LEVELZ, serverPlayerEntity.getScoreboardName(), score -> score.setScore(level));
					serverPlayerEntity.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
				} else {
					playerStatsManager.setSkillLevel(Skill.valueOf(skill.toUpperCase()), playerSkillLevel);
				}
				if (skill.equals("health")) {
					serverPlayerEntity.getAttribute(Attributes.MAX_HEALTH)
							.setBaseValue(ConfigInit.CONFIG.healthBase + (double) playerSkillLevel * ConfigInit.CONFIG.healthBonus);
					serverPlayerEntity.setHealth(serverPlayerEntity.getMaxHealth());
				} else if (skill.equals("strength")) {
					serverPlayerEntity.getAttribute(Attributes.ATTACK_DAMAGE)
							.setBaseValue(ConfigInit.CONFIG.attackBase + (double) playerSkillLevel * ConfigInit.CONFIG.attackBonus);
				} else if (skill.equals("agility")) {
					serverPlayerEntity.getAttribute(Attributes.MOVEMENT_SPEED)
							.setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerSkillLevel * ConfigInit.CONFIG.movementBonus);
				} else if (skill.equals("defense")) {
					serverPlayerEntity.getAttribute(Attributes.ARMOR).setBaseValue(ConfigInit.CONFIG.defenseBase + (double) playerSkillLevel * ConfigInit.CONFIG.defenseBonus);
				} else if (skill.equals("luck")) {
					serverPlayerEntity.getAttribute(Attributes.LUCK).setBaseValue(ConfigInit.CONFIG.luckBase + (double) playerSkillLevel * ConfigInit.CONFIG.luckBonus);
				}
			}
			PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, serverPlayerEntity);

			if (reference != 3) {
				source.sendSuccess(() -> Component.translatable("commands.playerstats.changed", serverPlayerEntity.getDisplayName()), true);
			}
		}

		return targets.size();
	}

	private static int executeInfoMaterial(CommandSourceStack source) {
		if (source.getPlayer() != null && !source.getPlayer().getMainHandItem().isEmpty()) {
			Item item = source.getPlayer().getMainHandItem().getItem();
			Component text = null;

			if (item instanceof ArmorItem) {
				text = Component.nullToEmpty("Material id: \"" + ((ArmorItem) item).getMaterial().getName().toLowerCase() + "\"");
			}
			if (item instanceof TieredItem) {
				text = Component.nullToEmpty("Material id: \"" + ((TieredItem) item).getTier().toString().toLowerCase() + "\"");
			}
			source.getPlayer().sendSystemMessage(text != null ? text : Component.nullToEmpty(item.getDescription().getString() + " does not have a material id"));
		}

		return 1;
	}

	private static List<String> skillStrings() {
		return List.of("agility", "alchemy", "archery", "defense", "farming", "health", "luck", "mining", "smithing", "stamina", "strength", "trade", "level", "points", "experience");
	}
}
