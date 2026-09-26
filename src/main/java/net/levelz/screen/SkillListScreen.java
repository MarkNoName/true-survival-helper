package net.levelz.screen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.screen.widget.LineWidget;
import net.levelz.screen.widget.SkillUnlocks;
import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.truesurvivalhelper.GuiBackportExcluded;

@Environment(EnvType.CLIENT)
public class SkillListScreen extends Screen implements Tab, GuiBackportExcluded {
	private final int backgroundWidth = 200;
	private final int backgroundHeight = 215;
	private int x;
	private int y;

	private final String title;
	private final List<LineWidget> lines = new ArrayList<>();
	private List<SkillUnlocks.Unlock> unlocks = new ArrayList<>();
	private int lineIndex = 0;
	private boolean sortAlphabetical = false;
	private boolean draggingScrollbar = false;

	public SkillListScreen(String title) {
		super(title.equals("crafting") ? Component.translatable("text.levelz.crafting_info")
				: Component.translatable("text.levelz.locked_list", Component.translatable(String.format("spritetip.levelz.%s_skill", title))));
		this.title = title;
	}

	@Override
	protected void init() {
		super.init();
		this.x = (this.width - this.backgroundWidth) / 2;
		this.y = (this.height - this.backgroundHeight) / 2;
		this.unlocks = SkillUnlocks.getListUnlocks(this.title);
		sortUnlocks();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		context.blit(SkillInfoScreen.BACKGROUND_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
		if (this.lines.size() > 10) {
			int sliderY = this.lineIndex * 156 / (this.lines.size() - 10);
			context.blit(SkillInfoScreen.BACKGROUND_TEXTURE, this.x + 186, this.y + 20 + sliderY, 200, 0, 6, 31, 256, 256);
		} else {
			context.blit(SkillInfoScreen.BACKGROUND_TEXTURE, this.x + 186, this.y + 20, 206, 0, 6, 31, 256, 256);
		}
		boolean sortHovered = SkillScreen.isPointWithinBounds(this.x + 179, this.y + 4, 14, 14, mouseX, mouseY);
		context.blit(SkillScreen.ICON_TEXTURE, this.x + 179, this.y + 4, sortHovered ? 14 : 0, this.sortAlphabetical ? 180 : 166, 14, 14);

		context.drawString(this.font, this.getTitle(), this.x + 7, this.y + 7, SkillScreen.TEXT_COLOR, false);

		List<Component> tooltip = null;
		for (int i = 0; i < 10 && this.lineIndex + i < this.lines.size(); i++) {
			List<Component> lineTooltip = this.lines.get(this.lineIndex + i).render(context, this.font, this.x + 12, this.y + 24 + i * 18, mouseX, mouseY);
			if (lineTooltip != null) {
				tooltip = lineTooltip;
			}
		}

		super.render(context, mouseX, mouseY, delta);
		DrawTabHelper.drawTab(this.minecraft, context, this, this.x, this.y, mouseX, mouseY);
		if (tooltip != null) {
			context.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
			if (ConfigInit.CONFIG.switch_screen) {
				this.minecraft.setScreen(new SkillScreen());
			} else {
				this.onClose();
			}
			return true;
		} else if (KeyInit.screenKey.matches(keyCode, scanCode)) {
			this.minecraft.setScreen(new SkillScreen());
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		DrawTabHelper.onTabButtonClick(this.minecraft, this, this.x, this.y, mouseX, mouseY, false);
		if (this.lines.size() > 10 && SkillScreen.isPointWithinBounds(this.x + 186, this.y + 20, 6, 187, mouseX, mouseY)) {
			this.draggingScrollbar = true;
			updateScrollFromMouse(mouseY);
			return true;
		}
		if (SkillScreen.isPointWithinBounds(this.x + 179, this.y + 4, 14, 14, mouseX, mouseY)) {
			this.sortAlphabetical = !this.sortAlphabetical;
			sortUnlocks();
			this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (this.lines.size() > 10 && SkillScreen.isPointWithinBounds(this.x + 7, this.y + 19, 186, 189, mouseX, mouseY)) {
			this.lineIndex = Math.max(0, Math.min(this.lineIndex - (int) amount, this.lines.size() - 10));
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.draggingScrollbar) {
			updateScrollFromMouse(mouseY);
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.draggingScrollbar = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	public int getWidth() {
		return this.backgroundWidth;
	}

	public int getHeight() {
		return this.backgroundHeight;
	}

	private void sortUnlocks() {
		if (this.sortAlphabetical) {
			this.unlocks.sort(Comparator.comparing(unlock -> unlock.getName().getString(), String.CASE_INSENSITIVE_ORDER));
		} else {
			this.unlocks.sort(Comparator.comparingInt(SkillUnlocks.Unlock::level));
		}
		this.lines.clear();
		LineWidget.addUnlockLines(this.lines, this.unlocks);
		this.lineIndex = Math.max(0, Math.min(this.lineIndex, this.lines.size() - 10));
	}

	private void updateScrollFromMouse(double mouseY) {
		this.lineIndex = SkillScreen.computeRowFromMouse(mouseY, this.y + 20, 187, 31, this.lines.size() - 10);
	}
}
