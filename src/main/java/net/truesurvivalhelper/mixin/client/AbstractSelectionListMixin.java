package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.social.SocialInteractionsPlayerList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin {
	@Shadow
	protected int x0;
	@Shadow
	protected int x1;
	@Shadow
	protected int y0;
	@Shadow
	protected int y1;

	@Redirect(
		method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V",
			ordinal = 0
		)
	)
	private void guibackport$renderTintedListBackground(
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
		guiGraphics.fill(x, y, x + width, y + height, 0x70000000);
	}

	@Redirect(
		method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V",
			ordinal = 1
		)
	)
	private void guibackport$skipTopCap(
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
	}

	@Redirect(
		method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V",
			ordinal = 2
		)
	)
	private void guibackport$skipBottomCap(
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
	}

	@Redirect(
		method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(Lnet/minecraft/client/renderer/RenderType;IIIIIII)V"
		)
	)
	private void guibackport$skipFadeGradient(
		GuiGraphics guiGraphics,
		RenderType renderType,
		int x1,
		int y1,
		int x2,
		int y2,
		int colorFrom,
		int colorTo,
		int z
	) {
	}

	@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("TAIL"))
	private void guibackport$renderListSeparators(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		if ((Object) this instanceof SocialInteractionsPlayerList) {
			return;
		}

		guiGraphics.fill(this.x0, this.y0 - 2, this.x1, this.y0 - 1, 0x33FFFFFF);
		guiGraphics.fill(this.x0, this.y0 - 1, this.x1, this.y0, 0xBF000000);
		guiGraphics.fill(this.x0, this.y1, this.x1, this.y1 + 1, 0xBF000000);
		guiGraphics.fill(this.x0, this.y1 + 1, this.x1, this.y1 + 2, 0x33FFFFFF);
	}
}
