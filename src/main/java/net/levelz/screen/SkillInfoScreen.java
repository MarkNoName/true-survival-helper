package net.levelz.screen;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.screen.widget.LineWidget;
import net.levelz.screen.widget.SkillUnlocks;
import net.levelz.stats.Skill;
import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.truesurvivalhelper.GuiBackportExcluded;

@Environment(EnvType.CLIENT)
public class SkillInfoScreen extends Screen implements Tab, GuiBackportExcluded {
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("levelz:textures/gui/skills/skill_info_background.png");

	private final int backgroundWidth = 200;
	private final int backgroundHeight = 215;
	private int x;
	private int y;

	private final String title;
	private final List<LineWidget> lines = new ArrayList<>();
	private int lineIndex = 0;
	private boolean draggingScrollbar = false;

	public SkillInfoScreen(String title) {
		super(Component.translatable(String.format("spritetip.levelz.%s_skill", title)));
		this.title = title;
	}

	@Override
	protected void init() {
		super.init();
		this.x = (this.width - this.backgroundWidth) / 2;
		this.y = (this.height - this.backgroundHeight) / 2;

		this.lines.clear();
		List<Component> infoTexts = new ArrayList<>();
		List<Component> bonusTexts = new ArrayList<>();
		fillTexts(infoTexts, bonusTexts);
		addTextLines(Component.translatable("text.levelz.gui.skill_info"), infoTexts);
		addTextLines(Component.translatable("text.levelz.gui.bonus_info"), bonusTexts);
		for (Map.Entry<Integer, Map<Integer, List<SkillUnlocks.Unlock>>> category : SkillUnlocks.getSkillUnlocks(this.title).entrySet()) {
			this.lines.add(new LineWidget(Component.translatable(SkillUnlocks.CATEGORY_KEYS[category.getKey()])));
			for (Map.Entry<Integer, List<SkillUnlocks.Unlock>> level : category.getValue().entrySet()) {
				this.lines.add(new LineWidget(Component.translatable("text.levelz.gui.short_level", level.getKey())));
				LineWidget.addUnlockLines(this.lines, level.getValue());
			}
		}
		this.lineIndex = Math.max(0, Math.min(this.lineIndex, this.lines.size() - 10));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		context.blit(BACKGROUND_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
		if (this.lines.size() > 10) {
			int sliderY = this.lineIndex * 156 / (this.lines.size() - 10);
			context.blit(BACKGROUND_TEXTURE, this.x + 186, this.y + 20 + sliderY, 200, 0, 6, 31, 256, 256);
		} else {
			context.blit(BACKGROUND_TEXTURE, this.x + 186, this.y + 20, 206, 0, 6, 31, 256, 256);
		}

		context.drawString(this.font, this.getTitle(), this.x + 7, this.y + 7, SkillScreen.TEXT_COLOR, false);
		if (this.minecraft.player != null) {
			Skill skill = Skill.valueOf(this.title.toUpperCase());
			int level = ((PlayerStatsManagerAccess) this.minecraft.player).getPlayerStatsManager().getSkillLevel(skill);
			context.drawString(this.font, Component.translatable("text.levelz.gui.short_level", level), this.x + 11 + this.font.width(this.getTitle()), this.y + 7, SkillScreen.TEXT_COLOR,
					false);
		}

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

	private void updateScrollFromMouse(double mouseY) {
		this.lineIndex = SkillScreen.computeRowFromMouse(mouseY, this.y + 20, 187, 31, this.lines.size() - 10);
	}

	private void fillTexts(List<Component> infoTexts, List<Component> bonusTexts) {
		DecimalFormat format = new DecimalFormat("0.0");
		int maxLevel = ConfigInit.CONFIG.maxLevel;
		switch (this.title) {
		case "health":
			addText(infoTexts, "text.levelz.gui.health_info");
			break;
		case "strength":
			addText(infoTexts, "text.levelz.gui.strength_info");
			addText(bonusTexts, "text.levelz.gui.strength_bonus", maxLevel);
			break;
		case "defense":
			addText(infoTexts, "text.levelz.gui.defense_info");
			addText(bonusTexts, "text.levelz.gui.defense_bonus", maxLevel);
			break;
		case "archery":
			addText(infoTexts, "text.levelz.gui.archery_info");
			addText(bonusTexts, "text.levelz.gui.archery_bonus", maxLevel);
			break;
		case "agility":
			addText(bonusTexts, "text.levelz.gui.agility_bonus_1");
			addText(bonusTexts, "text.levelz.gui.agility_bonus_2", maxLevel);
			break;
		case "alchemy":
			addText(infoTexts, "text.levelz.gui.alchemy_info");
			addText(bonusTexts, "text.levelz.gui.alchemy_bonus", maxLevel);
			break;
		case "mining":
			addText(infoTexts, "text.levelz.mining_info_2_1", format.format(ConfigInit.CONFIG.miningOreChance * 100F));
			addText(infoTexts, "text.levelz.mining_info_2_2", format.format(ConfigInit.CONFIG.miningOreChance * 100F));
			addText(bonusTexts, "text.levelz.gui.mining_bonus", maxLevel);
			break;
		case "smithing":
			addText(infoTexts, "text.levelz.smithing_info_2_1", format.format(ConfigInit.CONFIG.smithingToolChance * 100F));
			addText(infoTexts, "text.levelz.smithing_info_2_2", format.format(ConfigInit.CONFIG.smithingToolChance * 100F));
			addText(infoTexts, "text.levelz.smithing_info_3_1", format.format(ConfigInit.CONFIG.smithingCostBonus * 100F));
			addText(infoTexts, "text.levelz.smithing_info_3_2", format.format(ConfigInit.CONFIG.smithingCostBonus * 100F));
			addText(bonusTexts, "text.levelz.gui.smithing_bonus", maxLevel);
			break;
		case "farming":
			addText(infoTexts, "text.levelz.gui.farming_info");
			addText(bonusTexts, "text.levelz.gui.farming_bonus", maxLevel);
			break;
		case "stamina":
			addText(infoTexts, "text.levelz.gui.stamina_info");
			addText(bonusTexts, "text.levelz.gui.stamina_bonus_1");
			addText(bonusTexts, "text.levelz.gui.stamina_bonus_2", new DecimalFormat("0.#").format(ConfigInit.CONFIG.staminaFoodBonus * 100F), maxLevel);
			break;
		case "trade":
			addText(infoTexts, "text.levelz.gui.trade_info_1");
			addText(infoTexts, "text.levelz.gui.trade_info_2");
			addText(bonusTexts, "text.levelz.gui.trade_bonus", maxLevel);
			break;
		case "luck":
			addText(infoTexts, "text.levelz.gui.luck_info");
			addText(bonusTexts, "text.levelz.gui.luck_bonus", maxLevel);
			break;
		default:
			break;
		}
	}

	private void addTextLines(Component header, List<Component> texts) {
		if (texts.isEmpty()) {
			return;
		}
		this.lines.add(new LineWidget(header));
		for (Component text : texts) {
			for (FormattedCharSequence line : this.font.split(text, 172)) {
				this.lines.add(new LineWidget(line));
			}
		}
	}

	private static void addText(List<Component> texts, String key, Object... args) {
		if (!Language.getInstance().has(key)) {
			return;
		}
		Component text = Component.translatable(key, args);
		if (!text.getString().isBlank()) {
			texts.add(text);
		}
	}
}
