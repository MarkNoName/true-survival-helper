package net.levelz.mixin.player;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.CriteriaInit;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.scores.Score;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

import com.mojang.authlib.GameProfile;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {
	@Shadow
	@Final
	private MinecraftServer server;

	@Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addNewPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void onPlayerConnectMixin(Connection connection, ServerPlayer player, CallbackInfo info, GameProfile gameProfile, GameProfileCache userCache, String string,
			CompoundTag nbtCompound) {
		PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
		boolean isFirstTimeJoin = nbtCompound == null;
		if (isFirstTimeJoin && server != null && (server.getWorldData().worldGenOptions().generateBonusChest() || ConfigInit.CONFIG.enableStartPoints)) {
			playerStatsManager.setSkillPoints(ConfigInit.CONFIG.startPoints);
		}
		PlayerStatsServerPacket.writeS2CListPacket(player);
		if (isFirstTimeJoin) {
			player.setHealth(player.getMaxHealth());
		}
		PlayerStatsServerPacket.writeS2CStrengthPacket(player);
	}

	@Inject(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addRespawnedPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void respawnPlayerMixin(ServerPlayer player, boolean alive, CallbackInfoReturnable<ServerPlayer> info, BlockPos blockPos, float f, boolean bl, ServerLevel serverWorld,
			Optional<Object> optional, ServerLevel serverWorld2, ServerPlayer serverPlayerEntity) {
		if (alive || !ConfigInit.CONFIG.hardMode) {
			PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
			PlayerStatsManager serverPlayerStatsManager = ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager();
			PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, serverPlayerEntity);
			serverPlayerEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(player.getAttributeBaseValue(Attributes.MAX_HEALTH));
			serverPlayerEntity.setHealth(serverPlayerEntity.getMaxHealth());
			serverPlayerEntity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE));
			serverPlayerEntity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(player.getAttributeBaseValue(Attributes.MOVEMENT_SPEED));
			serverPlayerEntity.getAttribute(Attributes.ARMOR).setBaseValue(player.getAttributeBaseValue(Attributes.ARMOR));
			serverPlayerEntity.getAttribute(Attributes.LUCK).setBaseValue(player.getAttributeBaseValue(Attributes.LUCK));
			PlayerStatsServerPacket.writeS2CStrengthPacket(serverPlayerEntity);
			boolean keepInventory = serverWorld.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
			serverPlayerStatsManager.setLevelProgress(keepInventory ? playerStatsManager.getLevelProgress() : ConfigInit.CONFIG.resetCurrentXP ? 0 : playerStatsManager.getLevelProgress());
			serverPlayerStatsManager
					.setTotalLevelExperience(keepInventory ? playerStatsManager.getTotalLevelExperience() : ConfigInit.CONFIG.resetCurrentXP ? 0 : playerStatsManager.getTotalLevelExperience());
			serverPlayerStatsManager.setOverallLevel(playerStatsManager.getOverallLevel());
			serverPlayerStatsManager.setSkillPoints(playerStatsManager.getSkillPoints());
			for (Skill skill : Skill.values()) {
				serverPlayerStatsManager.setSkillLevel(skill, playerStatsManager.getSkillLevel(skill));
			}
		}
		if (ConfigInit.CONFIG.hardMode) {
			serverPlayerEntity.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
			serverPlayerEntity.getScoreboard().forAllObjectives(CriteriaInit.LEVELZ, serverPlayerEntity.getScoreboardName(), Score::reset);
		}
	}
}
