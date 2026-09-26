package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.truesurvivalhelper.GuiBackportTintStart;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin implements GuiBackportTintStart {
	@Shadow
	private TabNavigationBar tabNavigationBar;

	@Override
	public int guibackport$getTintStartY() {
		return this.tabNavigationBar != null ? this.tabNavigationBar.getRectangle().bottom() : 0;
	}

	@Redirect(
		method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
		)
	)
	private void guibackport$renderTranslucentFooterSeparator(
		GuiGraphics guiGraphics,
		ResourceLocation resourceLocation,
		int x,
		int y,
		float u,
		float v,
		int width,
		int height,
		int textureWidth,
		int textureHeight
	) {
		guiGraphics.fill(x, y, x + width, y + 1, 0xBF000000);
		guiGraphics.fill(x, y + 1, x + width, y + 2, 0x33FFFFFF);
	}

	@Inject(method = "renderDirtBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"), cancellable = true)
	private void guibackport$skipDirtBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
		ci.cancel();
	}
}
