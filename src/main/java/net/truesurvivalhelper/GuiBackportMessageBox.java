package net.truesurvivalhelper;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class GuiBackportMessageBox {
	private static final int PADDING = 12;

	private GuiBackportMessageBox() {
	}

	public static void renderBoxedCenteredString(GuiGraphics guiGraphics, Font font, Component component, int x, int y, int color) {
		int textWidth = font.width(component);
		int boxLeft = x - textWidth / 2 - PADDING;
		int boxTop = y - PADDING;
		int boxWidth = textWidth + PADDING * 2;
		int boxHeight = font.lineHeight + PADDING * 2;

		guiGraphics.fill(boxLeft + 1, boxTop, boxLeft + boxWidth, boxTop + boxHeight, 0xFF000000);
		guiGraphics.renderOutline(boxLeft, boxTop, boxWidth, boxHeight, 0xFFFFFFFF);
		guiGraphics.drawCenteredString(font, component, x, y, color);
	}
}
