package net.truesurvivalhelper.mixin.levelz;

import net.levelz.screen.widget.SkillScrollableWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SkillScrollableWidget.class)
public abstract class SkillScrollableWidgetMixin {
	@Shadow
	private String title;

	@Redirect(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I", ordinal = 6))
	private int tsh$hideUnlockableSkillsHeader(GuiGraphics context, Font font, Component text, int x, int y, int color, boolean dropShadow) {
		return this.title.equals("health") ? 0 : context.drawString(font, text, x, y, color, dropShadow);
	}

	@Redirect(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I", ordinal = 10))
	private int tsh$hideMaxLevelHeader(GuiGraphics context, Font font, Component text, int x, int y, int color, boolean dropShadow) {
		return this.title.equals("health") ? 0 : context.drawString(font, text, x, y, color, dropShadow);
	}

	@Redirect(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I", ordinal = 11))
	private int tsh$hideMaxLevelLine1(GuiGraphics context, Font font, Component text, int x, int y, int color, boolean dropShadow) {
		return this.title.equals("health") ? 0 : context.drawString(font, text, x, y, color, dropShadow);
	}

	@Redirect(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I", ordinal = 12))
	private int tsh$hideMaxLevelLine2(GuiGraphics context, Font font, Component text, int x, int y, int color, boolean dropShadow) {
		return this.title.equals("health") ? 0 : context.drawString(font, text, x, y, color, dropShadow);
	}
}
