package net.truesurvivalhelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public class GuiBackportSimpleOptionsList extends ContainerObjectSelectionList<GuiBackportSimpleOptionsList.Entry> {
	public GuiBackportSimpleOptionsList(Minecraft minecraft, int width, int height, int y0, int y1) {
		super(minecraft, width, height, y0, y1, 25);
		this.centerListVertically = false;
	}

	public void addPairedRows(List<AbstractWidget> widgets) {
		int leftX = this.width / 2 - 155;
		int rightX = leftX + 160;
		for (int i = 0; i < widgets.size(); i += 2) {
			AbstractWidget left = widgets.get(i);
			left.setX(leftX);
			AbstractWidget right = i + 1 < widgets.size() ? widgets.get(i + 1) : null;
			if (right != null) {
				right.setX(rightX);
			}

			this.addEntry(new Entry(left, right));
		}
	}

	@Override
	public int getRowWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}

	protected static final class Entry extends ContainerObjectSelectionList.Entry<Entry> {
		private final List<AbstractWidget> widgets;

		Entry(AbstractWidget left, AbstractWidget right) {
			this.widgets = new ArrayList<>(2);
			this.widgets.add(left);
			if (right != null) {
				this.widgets.add(right);
			}
		}

		@Override
		public void render(
			GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick
		) {
			for (AbstractWidget widget : this.widgets) {
				widget.setY(top);
				widget.render(guiGraphics, mouseX, mouseY, partialTick);
			}
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return this.widgets;
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return this.widgets;
		}
	}
}
