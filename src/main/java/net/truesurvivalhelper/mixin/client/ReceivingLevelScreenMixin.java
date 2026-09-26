package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.truesurvivalhelper.GuiBackportAlwaysPanorama;
import net.truesurvivalhelper.GuiBackportMessageBox;
import net.truesurvivalhelper.GuiBackportPortalReason;

@Mixin(ReceivingLevelScreen.class)
public abstract class ReceivingLevelScreenMixin implements GuiBackportAlwaysPanorama, GuiBackportPortalReason {
	@Unique
	private int guibackport$portalReason = OTHER;
	@Unique
	private TextureAtlasSprite guibackport$cachedNetherPortalSprite;

	@Override
	public int guibackport$getPortalReason() {
		return this.guibackport$portalReason;
	}

	@Override
	public void guibackport$setPortalReason(int reason) {
		this.guibackport$portalReason = reason;
	}

	@Override
	public void guibackport$renderPortalBackground(GuiGraphics guiGraphics) {
		Screen self = (Screen) (Object) this;
		if (this.guibackport$portalReason == NETHER_PORTAL) {
			if (this.guibackport$cachedNetherPortalSprite == null) {
				this.guibackport$cachedNetherPortalSprite = Minecraft.getInstance()
					.getBlockRenderer()
					.getBlockModelShaper()
					.getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
			}

			guiGraphics.blit(0, 0, -90, guiGraphics.guiWidth(), guiGraphics.guiHeight(), this.guibackport$cachedNetherPortalSprite);
		} else if (this.guibackport$portalReason == END_PORTAL) {
			guiGraphics.fill(RenderType.endPortal(), 0, 0, self.width, self.height, 0);
		}
	}

	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
		)
	)
	private void guibackport$renderBoxedMessage(GuiGraphics guiGraphics, Font font, Component component, int x, int y, int color) {
		GuiBackportMessageBox.renderBoxedCenteredString(guiGraphics, font, component, x, y, color);
	}
}
