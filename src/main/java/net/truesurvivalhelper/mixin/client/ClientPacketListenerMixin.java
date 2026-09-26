package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.truesurvivalhelper.GuiBackportPortalReason;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	@Shadow
	private ClientLevel level;

	@Inject(
		method = "handleRespawn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V",
			ordinal = 0,
			shift = At.Shift.AFTER
		)
	)
	private void guibackport$tagPortalReason(ClientboundRespawnPacket packet, CallbackInfo ci) {
		Screen screen = Minecraft.getInstance().screen;
		if (!(screen instanceof GuiBackportPortalReason portalScreen)) {
			return;
		}

		ResourceKey<Level> dimension = this.level.dimension();
		if (dimension == Level.NETHER) {
			portalScreen.guibackport$setPortalReason(GuiBackportPortalReason.NETHER_PORTAL);
		} else if (dimension == Level.END) {
			portalScreen.guibackport$setPortalReason(GuiBackportPortalReason.END_PORTAL);
		}
	}
}
