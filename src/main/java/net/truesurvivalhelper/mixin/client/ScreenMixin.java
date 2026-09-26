package net.truesurvivalhelper.mixin.client;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.truesurvivalhelper.GuiBackportAlwaysPanorama;
import net.truesurvivalhelper.GuiBackportBlurAccess;
import net.truesurvivalhelper.GuiBackportExcluded;
import net.truesurvivalhelper.GuiBackportPanorama;
import net.truesurvivalhelper.GuiBackportPortalReason;
import net.truesurvivalhelper.GuiBackportTintStart;

@Mixin(Screen.class)
public abstract class ScreenMixin {
	@Shadow
	protected Minecraft minecraft;
	@Shadow
	public int width;
	@Shadow
	public int height;

	@Unique
	private long guibackport$lastPanoramaTime = Util.getMillis();

	@Inject(method = "renderWithTooltip", at = @At("HEAD"))
	private void guibackport$renderBlurredBackdrop(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		if (this.guibackport$isNoBackground()) {
			return;
		}

		if (this.guibackport$isGradientOnly()) {
			guiGraphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
			return;
		}

		if (this.guibackport$isExcluded()) {
			return;
		}

		if (this.minecraft.level == null || (Object) this instanceof GuiBackportAlwaysPanorama) {
			GuiBackportPanorama.PANORAMA.render(this.guibackport$advancePanoramaTime(), 1.0F);
		}

		((GuiBackportBlurAccess) this.minecraft.gameRenderer).guibackport$processMenuBlur();
		this.guibackport$paintTint(guiGraphics);
	}

	@Unique
	private boolean guibackport$isNoBackground() {
		return (Object) this instanceof ChatScreen || (Object) this instanceof GameModeSwitcherScreen;
	}

	@Unique
	private boolean guibackport$isGradientOnly() {
		return (Object) this instanceof AbstractSignEditScreen
			|| (Object) this instanceof StructureBlockEditScreen
			|| (Object) this instanceof BookEditScreen
			|| (Object) this instanceof JigsawBlockEditScreen
			|| (Object) this instanceof AbstractCommandBlockEditScreen
			|| (Object) this instanceof BookViewScreen;
	}

	@Inject(method = "renderBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"), cancellable = true)
	private void guibackport$skipDirtBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
		if (this.guibackport$isExcluded()) {
			return;
		}

		ci.cancel();
	}

	@Unique
	private boolean guibackport$isExcluded() {
		if ((Object) this instanceof GuiBackportPortalReason portalScreen && portalScreen.guibackport$getPortalReason() != GuiBackportPortalReason.OTHER) {
			return true;
		}

		return (Object) this instanceof AbstractContainerScreen
			|| (Object) this instanceof TitleScreen
			|| (Object) this instanceof DeathScreen
			|| (Object) this instanceof GuiBackportExcluded;
	}

	@Inject(method = "renderDirtBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"), cancellable = true)
	private void guibackport$skipDirtBackground2(GuiGraphics guiGraphics, CallbackInfo ci) {
		if ((Object) this instanceof GuiBackportPortalReason portalScreen && portalScreen.guibackport$getPortalReason() != GuiBackportPortalReason.OTHER) {
			portalScreen.guibackport$renderPortalBackground(guiGraphics);
			ci.cancel();
			return;
		}

		if (this.guibackport$isExcluded()) {
			return;
		}

		ci.cancel();
	}

	@Unique
	private void guibackport$paintTint(GuiGraphics guiGraphics) {
		int startY = (Object) this instanceof GuiBackportTintStart tintStart ? tintStart.guibackport$getTintStartY() : 0;
		guiGraphics.fill(0, startY, this.width, this.height, 0x40000000);
	}

	@Unique
	private float guibackport$advancePanoramaTime() {
		long now = Util.getMillis();
		float delta = (float) (now - this.guibackport$lastPanoramaTime) / 50.0F;
		this.guibackport$lastPanoramaTime = now;
		return delta > 7.0F ? 0.5F : delta;
	}
}
