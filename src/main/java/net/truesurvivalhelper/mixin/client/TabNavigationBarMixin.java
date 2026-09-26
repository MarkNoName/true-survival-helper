package net.truesurvivalhelper.mixin.client;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TabNavigationBar.class)
public abstract class TabNavigationBarMixin {
	@Shadow
	@Final
	private ImmutableList<TabButton> tabButtons;

	@Redirect(
		method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V")
	)
	private void guibackport$skipOpaqueTabBarBackground(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
	}

	@Redirect(
		method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"
		)
	)
	private void guibackport$renderHeaderSeparator(
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
		int lineY = y;
		if (!this.tabButtons.isEmpty()) {
			TabButton first = this.tabButtons.get(0);
			lineY = first.getY() + first.getHeight() - 2;
		}

		int cursor = x;
		for (TabButton tabButton : this.tabButtons) {
			int tabX0 = tabButton.getX();
			int tabX1 = tabX0 + tabButton.getWidth();
			if (tabX0 > cursor) {
				guiGraphics.fill(cursor, lineY, tabX0, lineY + 1, 0x33FFFFFF);
				guiGraphics.fill(cursor, lineY + 1, tabX0, lineY + 2, 0xBF000000);
			}
			cursor = tabX1;
		}
		if (cursor < x + width) {
			guiGraphics.fill(cursor, lineY, x + width, lineY + 1, 0x33FFFFFF);
			guiGraphics.fill(cursor, lineY + 1, x + width, lineY + 2, 0xBF000000);
		}
	}
}
