package net.levelz.mixin.player;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.levelz.access.PlayerListAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;

@Mixin(ClientboundPlayerInfoUpdatePacket.class)
public abstract class PlayerListS2CPacketMixin implements PlayerListAccess {
	@Unique
	private Map<UUID, Integer> levelMap = new HashMap<UUID, Integer>();

	@Inject(method = "<init>(Ljava/util/EnumSet;Ljava/util/Collection;)V", at = @At("TAIL"))
	public void playerListS2CPacketMixin(EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions, Collection<ServerPlayer> players, CallbackInfo info) {
		players.forEach((player) -> {
			levelMap.put(player.getUUID(), ((PlayerStatsManagerAccess) player).getPlayerStatsManager().getOverallLevel());
		});
	}

	@Inject(method = "<init>(Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket$Action;Lnet/minecraft/server/level/ServerPlayer;)V", at = @At("TAIL"))
	public void playerListS2CPacketMixin(ClientboundPlayerInfoUpdatePacket.Action action, ServerPlayer player, CallbackInfo info) {
		levelMap.put(player.getUUID(), ((PlayerStatsManagerAccess) player).getPlayerStatsManager().getOverallLevel());
	}

	@Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
	public void playerListS2CPacketMixin(FriendlyByteBuf buf, CallbackInfo info) {
		levelMap = buf.readMap(FriendlyByteBuf::readUUID, FriendlyByteBuf::readInt);
	}

	@Inject(method = "write", at = @At("TAIL"))
	private void writeMixin(FriendlyByteBuf buf, CallbackInfo info) {
		buf.writeMap(levelMap, FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeInt);
	}

	@Override
	public Map<UUID, Integer> getLevelMap() {
		return levelMap;
	}
}
