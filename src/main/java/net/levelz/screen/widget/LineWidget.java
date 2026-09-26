package net.levelz.screen.widget;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.screen.SkillScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

@Environment(EnvType.CLIENT)
public class LineWidget {
	@Nullable
	private final FormattedCharSequence text;
	private final List<SkillUnlocks.Unlock> unlocks;

	public LineWidget(Component text) {
		this(text.getVisualOrderText());
	}

	public LineWidget(FormattedCharSequence text) {
		this.text = text;
		this.unlocks = List.of();
	}

	public LineWidget(List<SkillUnlocks.Unlock> unlocks) {
		this.text = null;
		this.unlocks = unlocks;
	}

	@Nullable
	public List<Component> render(GuiGraphics context, Font textRenderer, int x, int y, int mouseX, int mouseY) {
		if (this.text != null) {
			context.drawString(textRenderer, this.text, x, y + 4, SkillScreen.TEXT_COLOR, false);
			return null;
		}
		List<Component> tooltip = null;
		int separator = 0;
		for (SkillUnlocks.Unlock unlock : this.unlocks) {
			context.blit(SkillScreen.ICON_TEXTURE, x + separator - 1, y - 1, 0, 148, 18, 18);
			if (unlock.sprite() != null) {
				context.blit(unlock.sprite(), x + separator, y, 0, 0, 16, 16, 16, 16);
			} else {
				context.renderItem(unlock.stack(), x + separator, y);
			}
			if (tooltip == null && SkillScreen.isPointWithinBounds(x + separator, y, 16, 16, mouseX, mouseY)) {
				tooltip = new ArrayList<>(unlock.names());
				tooltip.add(Component.translatable(String.format("spritetip.levelz.%s_skill", unlock.skill())).append(" ").append(Component.translatable("text.levelz.gui.short_level", unlock.level())));
			}
			separator += 18;
		}
		return tooltip;
	}

	public static void addUnlockLines(List<LineWidget> lines, List<SkillUnlocks.Unlock> unlocks) {
		List<SkillUnlocks.Unlock> row = new ArrayList<>();
		int start = 0;
		while (start < unlocks.size()) {
			int end = start + 1;
			while (end < unlocks.size() && unlocks.get(end).group() == unlocks.get(start).group()) {
				end++;
			}
			if (end - start > 1) {
				addRow(lines, row);
				for (int i = start; i < end; i += 9) {
					lines.add(new LineWidget(new ArrayList<>(unlocks.subList(i, Math.min(i + 9, end)))));
				}
			} else {
				row.add(unlocks.get(start));
				if (row.size() == 9) {
					addRow(lines, row);
				}
			}
			start = end;
		}
		addRow(lines, row);
	}

	private static void addRow(List<LineWidget> lines, List<SkillUnlocks.Unlock> row) {
		if (!row.isEmpty()) {
			lines.add(new LineWidget(new ArrayList<>(row)));
			row.clear();
		}
	}
}
