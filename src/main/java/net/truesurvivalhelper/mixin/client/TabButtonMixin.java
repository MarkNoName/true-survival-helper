package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TabButton.class)
public abstract class TabButtonMixin {
	@Shadow
	private void renderFocusUnderline(GuiGraphics guiGraphics, Font font, int color) {
		throw new AssertionError("shadowed");
	}

	@Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
	private void guibackport$renderFlatTab(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		TabButton self = (TabButton) (Object) this;
		AbstractWidget widget = (AbstractWidget) (Object) this;

		int x0 = widget.getX();
		int y0 = widget.getY();
		int x1 = x0 + widget.getWidth();
		int y1 = y0 + widget.getHeight();

		int outer = 0xBF000000;
		int inner = 0x33FFFFFF;
		int sideInner = widget.isHoveredOrFocused() ? 0xFFFFFFFF : inner;

		int top = self.isSelected() ? y0 : y0 + 4;

		guiGraphics.fill(x0, top, x1, top + 1, outer);
		guiGraphics.fill(x0, top + 1, x0 + 1, top + 2, outer);
		guiGraphics.fill(x0 + 1, top + 1, x1 - 1, top + 2, sideInner);
		guiGraphics.fill(x1 - 1, top + 1, x1, top + 2, outer);

		guiGraphics.fill(x0, top + 2, x0 + 1, y1 - 2, outer);
		guiGraphics.fill(x0 + 1, top + 2, x0 + 2, y1 - 2, sideInner);
		guiGraphics.fill(x1 - 2, top + 2, x1 - 1, y1 - 2, sideInner);
		guiGraphics.fill(x1 - 1, top + 2, x1, y1 - 2, outer);

		if (self.isSelected()) {
			guiGraphics.fill(x0, y1 - 2, x0 + 1, y1 - 1, inner);
			guiGraphics.fill(x0 + 1, y1 - 2, x0 + 2, y1 - 1, sideInner);
			guiGraphics.fill(x1 - 2, y1 - 2, x1 - 1, y1 - 1, sideInner);
			guiGraphics.fill(x1 - 1, y1 - 2, x1, y1 - 1, inner);
			guiGraphics.fill(x0, y1 - 1, x0 + 2, y1, outer);
			guiGraphics.fill(x1 - 2, y1 - 1, x1, y1, outer);
			guiGraphics.fill(x0 + 2, top + 2, x1 - 2, y1, 0x40000000);
		} else {
			guiGraphics.fill(x0, y1 - 2, x1, y1 - 1, inner);
			guiGraphics.fill(x0, y1 - 1, x1, y1, outer);
			guiGraphics.fill(x0 + 2, top + 2, x1 - 2, y1 - 2, 0xDB000000);
			if (widget.isHoveredOrFocused()) {
				guiGraphics.fill(x0 + 1, y1 - 3, x1 - 1, y1 - 2, 0xFFFFFFFF);
			}
		}

		Font font = Minecraft.getInstance().font;
		int color = widget.active ? -1 : -6250336;
		self.renderString(guiGraphics, font, color);
		if (self.isSelected()) {
			this.renderFocusUnderline(guiGraphics, font, color);
		}

		ci.cancel();
	}
}
