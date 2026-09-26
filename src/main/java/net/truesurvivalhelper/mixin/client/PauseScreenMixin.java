package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.PauseScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin {
	@Redirect(
		method = "createPauseMenu",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
			ordinal = 2
		)
	)
	private LayoutElement guibackport$skipSendFeedbackButton(GridLayout.RowHelper rowHelper, LayoutElement layoutElement) {
		return layoutElement;
	}

	@Redirect(
		method = "createPauseMenu",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
			ordinal = 3
		)
	)
	private LayoutElement guibackport$skipReportBugsButton(GridLayout.RowHelper rowHelper, LayoutElement layoutElement) {
		return layoutElement;
	}
}
