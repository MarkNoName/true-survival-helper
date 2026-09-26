package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.truesurvivalhelper.GuiBackportMessageBox;

@Mixin(ProgressScreen.class)
public abstract class ProgressScreenMixin {
	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V",
			ordinal = 0
		)
	)
	private void guibackport$renderBoxedHeader(GuiGraphics guiGraphics, Font font, Component component, int x, int y, int color) {
		GuiBackportMessageBox.renderBoxedCenteredString(guiGraphics, font, component, x, y, color);
	}
}
