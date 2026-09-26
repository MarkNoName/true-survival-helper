package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.truesurvivalhelper.GuiBackportAlwaysPanorama;
import net.truesurvivalhelper.GuiBackportMessageBox;

@Mixin(GenericDirtMessageScreen.class)
public abstract class GenericDirtMessageScreenMixin implements GuiBackportAlwaysPanorama {
	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
		)
	)
	private void guibackport$renderBoxedMessage(GuiGraphics guiGraphics, Font font, Component component, int x, int y, int color) {
		Screen self = (Screen) (Object) this;
		int centerY = self.height / 2 - font.lineHeight / 2;
		GuiBackportMessageBox.renderBoxedCenteredString(guiGraphics, font, component, x, centerY, color);
	}
}
