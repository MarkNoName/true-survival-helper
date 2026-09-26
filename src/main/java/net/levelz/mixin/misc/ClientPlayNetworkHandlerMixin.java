package net.levelz.mixin.misc;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerListAccess;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {
	@Inject(method = "handlePlayerInfoUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/social/PlayerSocialManager;addPlayer(Lnet/minecraft/client/multiplayer/PlayerInfo;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void onPlayerListMixin(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo info, Iterator<ClientboundPlayerInfoUpdatePacket.Entry> var2, ClientboundPlayerInfoUpdatePacket.Entry entry, PlayerInfo playerListEntry) {
		((PlayerListAccess) playerListEntry).setLevel(((PlayerListAccess) packet).getLevelMap().get(playerListEntry.getProfile().getId()));
	}

	@Inject(method = "handlePlayerInfoUpdate", at = @At(value = "INVOKE", target = "Ljava/util/EnumSet;iterator()Ljava/util/Iterator;"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void onPlayerListGameModeMixin(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo info, Iterator<ClientboundPlayerInfoUpdatePacket.Entry> var2, ClientboundPlayerInfoUpdatePacket.Entry entry, PlayerInfo playerListEntry) {
		if (packet.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE)) {
			((PlayerListAccess) playerListEntry).setLevel(((PlayerListAccess) packet).getLevelMap().get(playerListEntry.getProfile().getId()));
		}
	}
}
