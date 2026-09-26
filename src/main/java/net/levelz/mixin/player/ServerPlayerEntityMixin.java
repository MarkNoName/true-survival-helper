package net.levelz.mixin.player;

import com.mojang.authlib.GameProfile;

import net.levelz.stats.Skill;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.CriteriaInit;
import net.levelz.network.PlayerStatsServerPacket;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Score;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements PlayerSyncAccess {
	private PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) this).getPlayerStatsManager();
	private int syncedLevelExperience = -99999999;
	private boolean syncTeleportStats = false;
	private int tinySyncTicker = 0;
	@Unique
	private static final int LEVEL_UP_SOUND_COOLDOWN = 100;
	@Unique
	private int levelUpSoundTick = -LEVEL_UP_SOUND_COOLDOWN;

	private ServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void initMixin(MinecraftServer server, ServerLevel world, GameProfile profile, CallbackInfo info) {
		ServerPlayer serverPlayerEntity = (ServerPlayer) (Object) this;
		serverPlayerEntity.getAttribute(Attributes.MAX_HEALTH)
				.setBaseValue(ConfigInit.CONFIG.healthBase + (double) playerStatsManager.getSkillLevel(Skill.HEALTH) * ConfigInit.CONFIG.healthBonus);
		serverPlayerEntity.getAttribute(Attributes.MOVEMENT_SPEED)
				.setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerStatsManager.getSkillLevel(Skill.AGILITY) * ConfigInit.CONFIG.movementBonus);
		serverPlayerEntity.getAttribute(Attributes.ATTACK_DAMAGE)
				.setBaseValue(ConfigInit.CONFIG.attackBase + (double) playerStatsManager.getSkillLevel(Skill.STRENGTH) * ConfigInit.CONFIG.attackBonus);
		serverPlayerEntity.getAttribute(Attributes.ARMOR)
				.setBaseValue(ConfigInit.CONFIG.defenseBase + (double) playerStatsManager.getSkillLevel(Skill.DEFENSE) * ConfigInit.CONFIG.defenseBonus);
		serverPlayerEntity.getAttribute(Attributes.LUCK)
				.setBaseValue(ConfigInit.CONFIG.luckBase + (double) playerStatsManager.getSkillLevel(Skill.LUCK) * ConfigInit.CONFIG.luckBonus);
	}

	@Override
	public void addLevelExperience(int experience) {
		ServerPlayer playerEntity = (ServerPlayer) (Object) this;
		if (!ConfigInit.CONFIG.useIndependentExp) {
			playerEntity.giveExperiencePoints(experience);
			return;
		}
		if (!playerStatsManager.isMaxLevel()) {
			playerStatsManager.setLevelProgress(playerStatsManager.getLevelProgress() + Math.max((float) experience / playerStatsManager.getNextLevelExperience(), 0));
			playerStatsManager.setTotalLevelExperience(Mth.clamp(playerStatsManager.getTotalLevelExperience() + experience, 0, Integer.MAX_VALUE));
			levelUp(ConfigInit.CONFIG.overallMaxLevel, true, false);
		}
	}

	@Override
	public void levelUp(int levels, boolean deductXp, boolean ignoreMaxLevel) {
		if (levels == 0) {
			levels = Integer.MAX_VALUE;
		}
		ServerPlayer playerEntity = (ServerPlayer) (Object) this;
		for (int i = 0; i < levels; i++) {
			if (!ignoreMaxLevel && playerStatsManager.isMaxLevel()) {
				break;
			}
			if (deductXp) {
				if (playerStatsManager.getLevelProgress() < 1) {
					break;
				}
				int nextLevelExperience = playerStatsManager.getNextLevelExperience();
				if (!ConfigInit.CONFIG.useIndependentExp) {
					playerEntity.giveExperiencePoints(-nextLevelExperience);
				} else {
					playerStatsManager.setLevelProgress((playerStatsManager.getLevelProgress() - 1.0F) * nextLevelExperience);
				}
			}
			playerStatsManager.addExperienceLevels(1);
			playerStatsManager.setLevelProgress(playerStatsManager.getLevelProgress() / playerStatsManager.getNextLevelExperience());
			PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, playerEntity);
			PlayerStatsManager.onLevelUp(playerEntity, playerStatsManager.getOverallLevel());
			CriteriaInit.LEVEL_UP.trigger(playerEntity, playerStatsManager.getOverallLevel());
			playerEntity.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, playerEntity));
			playerEntity.getScoreboard().forAllObjectives(CriteriaInit.LEVELZ, this.getScoreboardName(), Score::increment);
			if (playerStatsManager.getOverallLevel() > 0 && this.tickCount - this.levelUpSoundTick >= LEVEL_UP_SOUND_COOLDOWN) {
				this.levelUpSoundTick = this.tickCount;
				playerEntity.level().playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.PLAYER_LEVELUP, playerEntity.getSoundSource(), 1.0F, 1.0F);
			}
		}
		this.syncedLevelExperience = -1;
	}

	@Inject(method = "doTick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;totalExperience:I", ordinal = 0, shift = At.Shift.BEFORE))
	private void playerTickMixin(CallbackInfo info) {
		if (playerStatsManager.getTotalLevelExperience() != this.syncedLevelExperience) {
			this.syncedLevelExperience = playerStatsManager.getTotalLevelExperience();
			PlayerStatsServerPacket.writeS2CXPPacket(playerStatsManager, ((ServerPlayer) (Object) this));
			if (this.syncTeleportStats) {
				PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, (ServerPlayer) (Object) this);
				this.syncTeleportStats = false;
			}
		}
		if (this.tinySyncTicker > 0) {
			this.tinySyncTicker--;
			if (this.tinySyncTicker % 20 == 0) {
				syncStats(false);
			}
		}
	}

	@Inject(method = "initInventoryMenu", at = @At(value = "TAIL"))
	private void onSpawnMixin(CallbackInfo info) {
		PlayerStatsServerPacket.writeS2CSkillPacket(playerStatsManager, (ServerPlayer) (Object) this);
	}

	@Inject(method = "restoreFrom", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;lastSentExp:I", ordinal = 0))
	private void copyFromMixin(ServerPlayer oldPlayer, boolean alive, CallbackInfo info) {
		syncStats(false);
	}

	@Override
	public void syncStats(boolean syncDelay) {
		this.syncTeleportStats = true;
		this.syncedLevelExperience = -1;
		if (syncDelay)
			this.tinySyncTicker = 40;
	}
}
