package net.truesurvivalhelper.mixin.levelz;

import net.levelz.screen.widget.SkillScrollableWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Hides the "Unlockable Skills / Level {maxLevel} / -" block LevelZ's own Health skill
 * info renders. Health has no real item/block unlocks (unlike e.g. Farming or Alchemy),
 * and this pack's healthAbsorptionBonus is 0, so that section only ever shows a
 * meaningless placeholder dash - see levelz.json5 and the "health_max_lvl_*" keys in
 * kubejs/assets/levelz/lang/en_us.json.
 *
 * Each @Redirect below no-ops one specific drawString call inside
 * SkillScrollableWidget#renderContents, identified by disassembling the shipped class
 * (javap -c): it makes exactly 13 calls to that method, and ordinals 6/10/11/12 are,
 * respectively, the "Unlockable Skills" header (confirmed by the "text.levelz.
 * general_info" ldc immediately preceding it), the "Level {maxLevel}" fallback header
 * (confirmed by the neighbouring ConfigInit.CONFIG.maxLevel field read), and
 * textList.get(6)/get(7) (the health_max_lvl_1/2 lines). Every other call ordinal, and
 * every other skill tab, is untouched - each handler only substitutes a no-op when
 * title.equals("health"), otherwise it calls straight through to the real drawString.
 */
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
