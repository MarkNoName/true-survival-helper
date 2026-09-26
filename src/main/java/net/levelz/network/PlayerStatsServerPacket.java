package net.levelz.network;

import java.util.ArrayList;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.data.LevelLists;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.CriteriaInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PlayerStatsServerPacket {
	public static final ResourceLocation STATS_INCREASE_PACKET = new ResourceLocation("levelz", "player_increase_stats");
	public static final ResourceLocation STATS_SYNC_PACKET = new ResourceLocation("levelz", "player_sync_stats");
	public static final ResourceLocation XP_PACKET = new ResourceLocation("levelz", "player_level_xp");
	public static final ResourceLocation LEVEL_PACKET = new ResourceLocation("levelz", "player_level_stats");
	public static final ResourceLocation LIST_PACKET = new ResourceLocation("levelz", "unlocking_list");
	public static final ResourceLocation STRENGTH_PACKET = new ResourceLocation("levelz", "strength_sync");
	public static final ResourceLocation RESET_PACKET = new ResourceLocation("levelz", "reset_skill");
	public static final ResourceLocation LEVEL_EXPERIENCE_ORB_PACKET = new ResourceLocation("levelz", "level_experience_orb");
	public static final ResourceLocation SEND_CONFIG_SYNC_PACKET = new ResourceLocation("levelz", "send_config_sync_packet");
	public static final ResourceLocation TAG_PACKET = new ResourceLocation("levelz", "tag_packet");
	public static final ResourceLocation SEND_TAG_PACKET = new ResourceLocation("levelz", "send_tag_packet");
	public static final ResourceLocation LEVEL_UP_BUTTON_PACKET = new ResourceLocation("levelz", "level_up_button");

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(STATS_INCREASE_PACKET, (server, player, handler, buffer, sender) -> {
			String skillString = buffer.readUtf().toUpperCase();
			int level = buffer.readInt();
			server.execute(() -> {
				PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
				if (playerStatsManager.getSkillPoints() - level >= 0) {
					Skill skill = Skill.valueOf(skillString);
					if (!ConfigInit.CONFIG.allowHigherSkillLevel && playerStatsManager.getSkillLevel(skill) >= ConfigInit.CONFIG.maxLevel) {
						return;
					}

					for (int i = 1; i <= level; i++) {
						CriteriaInit.SKILL_UP.trigger(player, skillString.toLowerCase(), playerStatsManager.getSkillLevel(skill) + level);
					}
					playerStatsManager.setSkillLevel(skill, playerStatsManager.getSkillLevel(skill) + level);
					playerStatsManager.setSkillPoints(playerStatsManager.getSkillPoints() - level);
					switch (skill) {
					case HEALTH -> {
						player.getAttribute(Attributes.MAX_HEALTH)
								.setBaseValue(player.getAttributeBaseValue(Attributes.MAX_HEALTH) + ConfigInit.CONFIG.healthBonus * level);
						player.setHealth(player.getHealth() + (float) ConfigInit.CONFIG.healthBonus * level);
					}
					case STRENGTH -> player.getAttribute(Attributes.ATTACK_DAMAGE)
							.setBaseValue(player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) + ConfigInit.CONFIG.attackBonus * level);
					case AGILITY -> player.getAttribute(Attributes.MOVEMENT_SPEED)
							.setBaseValue(player.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + ConfigInit.CONFIG.movementBonus * level);
					case DEFENSE -> player.getAttribute(Attributes.ARMOR)
							.setBaseValue(player.getAttributeBaseValue(Attributes.ARMOR) + ConfigInit.CONFIG.defenseBonus * level);
					case LUCK -> player.getAttribute(Attributes.LUCK)
							.setBaseValue(player.getAttributeBaseValue(Attributes.LUCK) + ConfigInit.CONFIG.luckBonus * level);
					case MINING -> syncLockedBlockList(playerStatsManager);
					case ALCHEMY -> syncLockedBrewingItemList(playerStatsManager);
					case SMITHING -> syncLockedSmithingItemList(playerStatsManager);
					default -> {
					}
					}
					syncLockedCraftingItemList(playerStatsManager);

					writeS2CSyncLevelPacket(playerStatsManager, player, skill);
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(SEND_TAG_PACKET, (server, player, handler, buffer, sender) -> {
			writeS2CTagPacket(player, buffer.readResourceLocation());
		});
		ServerPlayNetworking.registerGlobalReceiver(LEVEL_UP_BUTTON_PACKET, (server, player, handler, buffer, sender) -> {
			int levelUp = buffer.readInt();
			server.execute(() -> {
				((PlayerSyncAccess) player).levelUp(levelUp, true, false);
			});
		});
	}

	public static void writeS2CSyncLevelPacket(PlayerStatsManager playerStatsManager, ServerPlayer serverPlayerEntity, Skill skill) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeUtf(skill.name());
		buf.writeInt(playerStatsManager.getSkillLevel(skill));
		buf.writeInt(playerStatsManager.getSkillPoints());
		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(STATS_SYNC_PACKET, buf);
		serverPlayerEntity.connection.send(packet);
	}

	public static void writeS2CXPPacket(PlayerStatsManager playerStatsManager, ServerPlayer serverPlayerEntity) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeFloat(playerStatsManager.getLevelProgress());
		buf.writeInt(playerStatsManager.getTotalLevelExperience());
		buf.writeInt(playerStatsManager.getOverallLevel());
		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(XP_PACKET, buf);
		serverPlayerEntity.connection.send(packet);
	}

	public static void writeS2CSkillPacket(PlayerStatsManager playerStatsManager, ServerPlayer serverPlayerEntity) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeFloat(playerStatsManager.getLevelProgress());
		buf.writeInt(playerStatsManager.getTotalLevelExperience());
		buf.writeInt(playerStatsManager.getOverallLevel());
		buf.writeInt(playerStatsManager.getSkillPoints());
		for (Skill skill : Skill.values()) {
			buf.writeInt(playerStatsManager.getSkillLevel(skill));
		}

		syncLockedBlockList(playerStatsManager);
		syncLockedBrewingItemList(playerStatsManager);
		syncLockedSmithingItemList(playerStatsManager);
		syncLockedCraftingItemList(playerStatsManager);

		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(LEVEL_PACKET, buf);
		serverPlayerEntity.connection.send(packet);
	}

	public static void writeS2CStrengthPacket(ServerPlayer serverPlayerEntity) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeDouble(serverPlayerEntity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(STRENGTH_PACKET, buf);
		serverPlayerEntity.connection.send(packet);
	}

	public static void syncLockedBlockList(PlayerStatsManager playerStatsManager) {
		playerStatsManager.lockedBlockIds.clear();
		for (int i = 0; i < LevelLists.miningLevelList.size(); i++) {
			if (LevelLists.miningLevelList.get(i) > playerStatsManager.getSkillLevel(Skill.MINING)) {
				for (int u = 0; u < LevelLists.miningBlockList.get(i).size(); u++) {
					if (!playerStatsManager.lockedBlockIds.contains(LevelLists.miningBlockList.get(i).get(u)))
						playerStatsManager.lockedBlockIds.add(LevelLists.miningBlockList.get(i).get(u));
				}
			}
		}
	}

	public static void syncLockedBrewingItemList(PlayerStatsManager playerStatsManager) {
		playerStatsManager.lockedbrewingItemIds.clear();
		for (int i = 0; i < LevelLists.brewingLevelList.size(); i++) {
			if (LevelLists.brewingLevelList.get(i) > playerStatsManager.getSkillLevel(Skill.ALCHEMY)) {
				for (int u = 0; u < LevelLists.brewingItemList.get(i).size(); u++) {
					if (!playerStatsManager.lockedbrewingItemIds.contains(LevelLists.brewingItemList.get(i).get(u)))
						playerStatsManager.lockedbrewingItemIds.add(LevelLists.brewingItemList.get(i).get(u));
				}
			}
		}
	}

	public static void syncLockedSmithingItemList(PlayerStatsManager playerStatsManager) {
		playerStatsManager.lockedSmithingItemIds.clear();
		for (int i = 0; i < LevelLists.smithingLevelList.size(); i++) {
			if (LevelLists.smithingLevelList.get(i) > playerStatsManager.getSkillLevel(Skill.SMITHING)) {
				for (int u = 0; u < LevelLists.smithingItemList.get(i).size(); u++) {
					if (!playerStatsManager.lockedSmithingItemIds.contains(LevelLists.smithingItemList.get(i).get(u)))
						playerStatsManager.lockedSmithingItemIds.add(LevelLists.smithingItemList.get(i).get(u));
				}
			}
		}
	}

	public static void syncLockedCraftingItemList(PlayerStatsManager playerStatsManager) {
		playerStatsManager.lockedCraftingItemIds.clear();
		for (int i = 0; i < LevelLists.craftingLevelList.size(); i++) {
			if (LevelLists.craftingLevelList.get(i) > playerStatsManager.getSkillLevel(Skill.valueOf(LevelLists.craftingSkillList.get(i).toString().toUpperCase()))) {
				for (int u = 0; u < LevelLists.craftingItemList.get(i).size(); u++) {
					if (!playerStatsManager.lockedCraftingItemIds.contains(LevelLists.craftingItemList.get(i).get(u)))
						playerStatsManager.lockedCraftingItemIds.add(LevelLists.craftingItemList.get(i).get(u));
				}
			}
		}
	}

	public static void writeS2CTagPacket(ServerPlayer serverPlayerEntity, ResourceLocation identifier) {
	}

	public static void writeS2CListPacket(ServerPlayer serverPlayerEntity) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		for (int i = 0; i < LevelLists.getListNames().size(); i++) {
			String listName = LevelLists.getListNames().get(i);
			ArrayList<Object> list = LevelLists.getList(listName);
			for (int u = 0; u < list.size(); u++) {
				buf.writeUtf(list.get(u).toString());
			}
		}
		for (int k = 0; k < LevelLists.miningLevelList.size(); k++) {
			buf.writeUtf("mining:level");
			buf.writeUtf(LevelLists.miningLevelList.get(k).toString());
			for (int u = 0; u < LevelLists.miningBlockList.get(k).size(); u++) {
				buf.writeUtf(LevelLists.miningBlockList.get(k).get(u).toString());
			}
		}
		for (int k = 0; k < LevelLists.brewingLevelList.size(); k++) {
			buf.writeUtf("brewing:level");
			buf.writeUtf(LevelLists.brewingLevelList.get(k).toString());
			for (int u = 0; u < LevelLists.brewingItemList.get(k).size(); u++) {
				buf.writeUtf(LevelLists.brewingItemList.get(k).get(u).toString());
			}
		}
		for (int k = 0; k < LevelLists.smithingLevelList.size(); k++) {
			buf.writeUtf("smithing:level");
			buf.writeUtf(LevelLists.smithingLevelList.get(k).toString());
			for (int u = 0; u < LevelLists.smithingItemList.get(k).size(); u++) {
				buf.writeUtf(LevelLists.smithingItemList.get(k).get(u).toString());
			}
		}
		for (int k = 0; k < LevelLists.craftingLevelList.size(); k++) {
			buf.writeUtf("crafting:level");
			buf.writeUtf(LevelLists.craftingLevelList.get(k).toString());
			buf.writeUtf(LevelLists.craftingSkillList.get(k).toString());
			for (int u = 0; u < LevelLists.craftingItemList.get(k).size(); u++) {
				buf.writeUtf(LevelLists.craftingItemList.get(k).get(u).toString());
			}
		}

		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(LIST_PACKET, buf);
		serverPlayerEntity.connection.send(packet);
	}

	public static void writeS2CResetSkillPacket(ServerPlayer serverPlayerEntity, Skill skill) {
		PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager();
		int skillLevel = playerStatsManager.getSkillLevel(skill);
		switch (skill) {
		case HEALTH -> {
			serverPlayerEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(ConfigInit.CONFIG.healthBase + skillLevel * ConfigInit.CONFIG.healthBonus);
			serverPlayerEntity.setHealth(serverPlayerEntity.getMaxHealth());
		}
		case STRENGTH -> serverPlayerEntity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ConfigInit.CONFIG.attackBase + skillLevel * ConfigInit.CONFIG.attackBonus);
		case AGILITY -> serverPlayerEntity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ConfigInit.CONFIG.movementBase + skillLevel * ConfigInit.CONFIG.movementBonus);
		case DEFENSE -> serverPlayerEntity.getAttribute(Attributes.ARMOR).setBaseValue(ConfigInit.CONFIG.defenseBase + skillLevel * ConfigInit.CONFIG.defenseBonus);
		case LUCK -> serverPlayerEntity.getAttribute(Attributes.LUCK).setBaseValue(ConfigInit.CONFIG.luckBase + skillLevel * ConfigInit.CONFIG.luckBonus);
		default -> {
		}
		}
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeUtf(skill.name());
		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(RESET_PACKET, buf);
		serverPlayerEntity.connection.send(packet);
	}

	public Packet<ClientGamePacketListener> createS2CLevelExperienceOrbPacket(LevelExperienceOrbEntity levelExperienceOrbEntity) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeVarInt(levelExperienceOrbEntity.getId());
		buf.writeDouble(levelExperienceOrbEntity.getX());
		buf.writeDouble(levelExperienceOrbEntity.getY());
		buf.writeDouble(levelExperienceOrbEntity.getZ());
		buf.writeShort(levelExperienceOrbEntity.getExperienceAmount());
		return ServerPlayNetworking.createS2CPacket(LEVEL_EXPERIENCE_ORB_PACKET, buf);
	}
}
