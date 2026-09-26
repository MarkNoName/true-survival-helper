package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.gui.layouts.LayoutElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$MoreTab")
public abstract class CreateWorldMoreTabMixin {
	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
					ordinal = 1
			)
	)
	private LayoutElement tsh$skipExperimentsButton(net.minecraft.client.gui.layouts.GridLayout.RowHelper rowHelper, LayoutElement element) {
		return element;
	}
}
