package net.levelz.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.network.PlayerStatsClientPacket;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.truesurvivalhelper.GuiBackportExcluded;

@Environment(EnvType.CLIENT)
public class SkillScreen extends Screen implements Tab, GuiBackportExcluded {
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("levelz:textures/gui/skills/skill_background.png");
	public static final ResourceLocation ATTRIBUTE_BACKGROUND_TEXTURE = new ResourceLocation("levelz:textures/gui/skills/attribute_background.png");
	public static final ResourceLocation ICON_TEXTURE = new ResourceLocation("levelz:textures/gui/skills/icons.png");
	public static final int TEXT_COLOR = 0xAAAAAA;

	private static final String[] ATTRIBUTE_SPRITES = { "generic.max_health", "generic.armor", "generic.attack_damage", "generic.hunger", "generic.luck" };
	private static final Skill[] SKILL_ORDER = { Skill.HEALTH, Skill.STRENGTH, Skill.DEFENSE, Skill.ARCHERY, Skill.AGILITY, Skill.ALCHEMY, Skill.MINING, Skill.SMITHING, Skill.FARMING, Skill.STAMINA,
			Skill.TRADE, Skill.LUCK };

	private final WidgetButtonPage[] levelButtons = new WidgetButtonPage[12];
	private final List<WidgetButtonPage> infoButtons = new ArrayList<>();
	private final Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI).rotateLocalY(2.7f);

	private Player playerEntity;
	private PlayerStatsManager playerStatsManager;

	private final int backgroundWidth = 200;
	private final int backgroundHeight = 215;
	private int x;
	private int y;
	private int craftingButtonY = -1;
	private int miningButtonY = -1;
	private boolean showAttributes = false;
	private boolean turnClientPlayer = false;

	public SkillScreen() {
		super(Component.translatable("screen.levelz.skill_screen"));
	}

	@Override
	protected void init() {
		super.init();
		this.playerEntity = this.minecraft.player;
		this.playerStatsManager = ((PlayerStatsManagerAccess) this.playerEntity).getPlayerStatsManager();
		this.x = (this.width - this.backgroundWidth) / 2;
		this.y = (this.height - this.backgroundHeight) / 2;
		this.infoButtons.clear();

		for (int i = 0; i < this.levelButtons.length; i++) {
			final Skill skill = SKILL_ORDER[i];
			this.levelButtons[i] = this.addRenderableWidget(new WidgetButtonPage(getTileX(i) + 76, getTileY(i) + 4, 13, 13, 33, 42, true, true, null, button -> {
				int level = 1;
				if (((WidgetButtonPage) button).wasRightButtonClicked()) {
					level = 5;
				} else if (((WidgetButtonPage) button).wasMiddleButtonClicked()) {
					level = 10;
				}
				PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(this.playerStatsManager, skill, level);
			}));
		}
		updateLevelButtons();

		int buttonY = this.y + 29;
		this.craftingButtonY = -1;
		if (!LevelLists.craftingItemList.isEmpty()) {
			this.craftingButtonY = buttonY;
			buttonY += 16;
		}
		this.miningButtonY = -1;
		if (!LevelLists.miningLevelList.isEmpty()) {
			this.miningButtonY = buttonY;
			buttonY += 16;
		}

		if (!ConfigInit.CONFIG.useIndependentExp) {
			WidgetButtonPage levelUpButton = this.addRenderableWidget(new WidgetButtonPage(this.x + 179, buttonY, 13, 13, 33, 42, true, true, Component.translatable("text.levelz.level_up"), button -> {
				int level = 1;
				if (((WidgetButtonPage) button).wasRightButtonClicked()) {
					level = 5;
				} else if (((WidgetButtonPage) button).wasMiddleButtonClicked()) {
					level = 10;
				}
				PlayerStatsClientPacket.writeC2SLevelUpPacket(level);
			}));
			for (String line : Component.translatable("text.levelz.gui.level_up.tooltip").getString().split("\n")) {
				levelUpButton.addTooltip(Component.nullToEmpty(line));
			}
			levelUpButton.active = !this.playerStatsManager.isMaxLevel() && this.playerStatsManager.getNonIndependentExperience() >= this.playerStatsManager.getNextLevelExperience();
			this.infoButtons.add(levelUpButton);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		context.blit(BACKGROUND_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
		List<Component> tooltip = null;

		for (int i = 0; i < 12; i++) {
			Skill skill = SKILL_ORDER[i];
			int tileX = getTileX(i);
			int tileY = getTileY(i);
			context.blit(BACKGROUND_TEXTURE, tileX, tileY, 0, 215, 46, 20);
			context.blit(BACKGROUND_TEXTURE, tileX + 46, tileY, 42, 215, 46, 20);
			context.blit(ICON_TEXTURE, tileX + 3, tileY + 2, skill.ordinal() * 16, 16, 16, 16);
			Component currentLevelText = Component.translatable("text.levelz.gui.current_level", this.playerStatsManager.getSkillLevel(skill), ConfigInit.CONFIG.maxLevel);
			context.drawString(this.font, currentLevelText, tileX + 47 - this.font.width(currentLevelText) / 2, tileY + 7, TEXT_COLOR, false);
			if (isPointWithinBounds(tileX + 3, tileY + 2, 16, 16, mouseX, mouseY)) {
				tooltip = getSkillTooltip(skill);
			}
		}

		Component title = Component.translatable("text.levelz.gui.title", this.playerEntity.getName().getString());
		context.drawString(this.font, title, this.x + 118 - this.font.width(title) / 2, this.y + 7, TEXT_COLOR, false);

		context.blit(ICON_TEXTURE, this.x + 62, this.y + 21, 0, 100, 131, 5);
		int nextLevelExperience = this.playerStatsManager.getNextLevelExperience();
		float levelProgress;
		long experience;
		if (!ConfigInit.CONFIG.useIndependentExp) {
			experience = this.playerStatsManager.getNonIndependentExperience();
			levelProgress = Math.min((float) experience / nextLevelExperience, 1);
		} else {
			levelProgress = this.playerStatsManager.getLevelProgress();
			experience = (int) (nextLevelExperience * levelProgress);
		}
		context.blit(ICON_TEXTURE, this.x + 62, this.y + 21, 0, 105, (int) (130.0f * levelProgress), 5);
		Component currentXpText = Component.translatable("text.levelz.gui.current_xp", experience, nextLevelExperience);
		context.drawString(this.font, currentXpText, this.x + 127 - this.font.width(currentXpText) / 2, this.y + 30, TEXT_COLOR, false);

		context.drawString(this.font, Component.translatable("text.levelz.gui.level", this.playerStatsManager.getOverallLevel()), this.x + 62, this.y + 42, TEXT_COLOR, false);
		context.drawString(this.font, Component.translatable("text.levelz.gui.points", this.playerStatsManager.getSkillPoints()), this.x + 62, this.y + 54, TEXT_COLOR, false);

		if (this.showAttributes) {
			context.blit(ICON_TEXTURE, this.x + 178, this.y + 5, 30, 114, 15, 13);
			context.blit(ATTRIBUTE_BACKGROUND_TEXTURE, this.x + 202, this.y, 0, 0, 82, 215);
			context.drawString(this.font, Component.translatable("text.levelz.gui.attributes"), this.x + 214, this.y + 12, TEXT_COLOR, false);
			String[] values = getAttributeValues();
			for (int i = 0; i < ATTRIBUTE_SPRITES.length; i++) {
				context.blit(new ResourceLocation("levelz", "textures/gui/skills/sprites/" + ATTRIBUTE_SPRITES[i] + ".png"), this.x + 214, this.y + 27 + i * 12, 0, 0, 9, 9, 9, 9);
				context.drawString(this.font, Component.nullToEmpty(values[i]), this.x + 229, this.y + 27 + i * 12, TEXT_COLOR, false);
			}
		} else {
			context.blit(ICON_TEXTURE, this.x + 178, this.y + 5, 15, 114, 15, 13);
		}
		if (isPointWithinBounds(this.x + 178, this.y + 5, 15, 13, mouseX, mouseY)) {
			tooltip = List.of(Component.translatable("text.levelz.gui.attributes"));
		}
		if (this.craftingButtonY != -1) {
			boolean hovered = isPointWithinBounds(this.x + 178, this.craftingButtonY, 14, 13, mouseX, mouseY);
			context.blit(ICON_TEXTURE, this.x + 178, this.craftingButtonY, hovered ? 30 : 15, 80, 15, 13);
			if (hovered) {
				tooltip = List.of(Component.translatable("text.levelz.crafting_info"));
			}
		}
		if (this.miningButtonY != -1) {
			boolean hovered = isPointWithinBounds(this.x + 178, this.miningButtonY, 14, 13, mouseX, mouseY);
			context.blit(ICON_TEXTURE, this.x + 178, this.miningButtonY, hovered ? 75 : 60, 80, 15, 13);
			if (hovered) {
				tooltip = List.of(Component.translatable("text.levelz.locked_list", Component.translatable("spritetip.levelz.mining_skill")));
			}
		}

		drawPlayer(context);
		boolean leftArrowHovered = isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY);
		context.blit(ICON_TEXTURE, this.x + 9, this.y + 67, 0, leftArrowHovered ? 138 : 128, 15, 10);
		boolean rightArrowHovered = isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY);
		context.blit(ICON_TEXTURE, this.x + 41, this.y + 67, 15, rightArrowHovered ? 138 : 128, 15, 10);

		updateLevelButtons();
		super.render(context, mouseX, mouseY, delta);
		DrawTabHelper.drawTab(this.minecraft, context, this, this.x, this.y, mouseX, mouseY);

		for (WidgetButtonPage button : this.infoButtons) {
			if (button.isHovered()) {
				tooltip = button.getTooltipLines();
			}
		}
		if (tooltip != null && !tooltip.isEmpty()) {
			context.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.turnClientPlayer) {
			double mouseX = this.minecraft.mouseHandler.xpos() * (double) this.minecraft.getWindow().getGuiScaledWidth() / (double) this.minecraft.getWindow().getScreenWidth();
			double mouseY = this.minecraft.mouseHandler.ypos() * (double) this.minecraft.getWindow().getGuiScaledHeight() / (double) this.minecraft.getWindow().getScreenHeight();
			if (isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY)) {
				this.quaternionf.rotateLocalY(0.087f);
			} else if (isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY)) {
				this.quaternionf.rotateLocalY(-0.087f);
			} else {
				this.turnClientPlayer = false;
			}
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (KeyInit.screenKey.matches(keyCode, scanCode) || Objects.requireNonNull(minecraft).options.keyInventory.matches(keyCode, scanCode)) {
			this.onClose();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		DrawTabHelper.onTabButtonClick(this.minecraft, this, this.x, this.y, mouseX, mouseY, this.getFocused() != null);
		if (isPointWithinBounds(this.x + 178, this.y + 5, 15, 13, mouseX, mouseY)) {
			this.showAttributes = !this.showAttributes;
			playClickSound();
			return true;
		}
		if (this.craftingButtonY != -1 && isPointWithinBounds(this.x + 178, this.craftingButtonY, 14, 13, mouseX, mouseY)) {
			playClickSound();
			this.minecraft.setScreen(new SkillListScreen("crafting"));
			return true;
		}
		if (this.miningButtonY != -1 && isPointWithinBounds(this.x + 178, this.miningButtonY, 14, 13, mouseX, mouseY)) {
			playClickSound();
			this.minecraft.setScreen(new SkillListScreen("mining"));
			return true;
		}
		if (isPointWithinBounds(this.x + 9, this.y + 67, 15, 10, mouseX, mouseY) || isPointWithinBounds(this.x + 41, this.y + 67, 15, 10, mouseX, mouseY)) {
			this.turnClientPlayer = true;
			playClickSound();
			return true;
		}
		for (int i = 0; i < 12; i++) {
			if (isPointWithinBounds(getTileX(i) + 3, getTileY(i) + 2, 16, 16, mouseX, mouseY)) {
				playClickSound();
				this.minecraft.setScreen(new SkillInfoScreen(SKILL_ORDER[i].name().toLowerCase()));
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.turnClientPlayer = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	public int getWidth() {
		return this.backgroundWidth;
	}

	public int getHeight() {
		return this.backgroundHeight;
	}

	public static boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return pointX >= (double) (x - 1) && pointX < (double) (x + width + 1) && pointY >= (double) (y - 1) && pointY < (double) (y + height + 1);
	}

	public static int computeRowFromMouse(double mouseY, int trackY, int trackHeight, int thumbHeight, int maxRow) {
		if (maxRow <= 0) {
			return 0;
		}
		int usableTrack = trackHeight - thumbHeight;
		if (usableTrack <= 0) {
			return 0;
		}
		double relativeY = mouseY - trackY - thumbHeight / 2.0;
		int row = (int) Math.round(relativeY / usableTrack * maxRow);
		return Math.max(0, Math.min(maxRow, row));
	}

	private int getTileX(int index) {
		return this.x + (index % 2 == 0 ? 8 : 100);
	}

	private int getTileY(int index) {
		return this.y + 87 + index / 2 * 20;
	}

	private void playClickSound() {
		this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	private void updateLevelButtons() {
		boolean skillsAllMaxed = true;
		for (int i = 0; i < this.levelButtons.length; i++) {
			int skillLevel = this.playerStatsManager.getSkillLevel(SKILL_ORDER[i]);
			this.levelButtons[i].active = this.playerStatsManager.getSkillPoints() > 0 && skillLevel < ConfigInit.CONFIG.maxLevel;
			if (skillLevel < ConfigInit.CONFIG.maxLevel) {
				skillsAllMaxed = false;
			}
		}
		if (skillsAllMaxed && ConfigInit.CONFIG.allowHigherSkillLevel) {
			for (WidgetButtonPage levelButton : this.levelButtons) {
				levelButton.active = true;
			}
		}
	}

	private List<Component> getSkillTooltip(Skill skill) {
		String name = skill.name().toLowerCase();
		List<Component> tooltip = new ArrayList<>();
		tooltip.add(Component.translatable("spritetip.levelz." + name + "_skill"));
		for (int i = 1; i < 10; i++) {
			String translatable = "spritetip.levelz." + name + "_skill_info_" + i;
			Component line = Component.translatable(translatable);
			if (!line.getString().equals(translatable)) {
				tooltip.add(line);
			}
		}
		return tooltip;
	}

	private void drawPlayer(GuiGraphics context) {
		float bodyYaw = this.playerEntity.yBodyRot;
		float prevBodyYaw = this.playerEntity.yBodyRotO;
		float yaw = this.playerEntity.getYRot();
		float pitch = this.playerEntity.getXRot();
		float prevHeadYaw = this.playerEntity.yHeadRotO;
		float headYaw = this.playerEntity.yHeadRot;
		this.playerEntity.yBodyRot = 0.0F;
		this.playerEntity.yBodyRotO = 0.0F;
		this.playerEntity.setYRot(0.0F);
		this.playerEntity.setXRot(0.0F);
		this.playerEntity.yHeadRot = 0.0F;
		this.playerEntity.yHeadRotO = 0.0F;
		InventoryScreen.renderEntityInInventory(context, this.x + 33, this.y + 70, 30, new Quaternionf(this.quaternionf), null, this.playerEntity);
		this.playerEntity.yBodyRot = bodyYaw;
		this.playerEntity.yBodyRotO = prevBodyYaw;
		this.playerEntity.setYRot(yaw);
		this.playerEntity.setXRot(pitch);
		this.playerEntity.yHeadRotO = prevHeadYaw;
		this.playerEntity.yHeadRot = headYaw;
	}

	private String[] getAttributeValues() {
		return new String[] { String.valueOf(round(this.playerEntity.getMaxHealth())), String.valueOf(round(this.playerEntity.getAttributeValue(Attributes.ARMOR))),
				String.valueOf(round(getDamage())), String.valueOf(this.playerEntity.getFoodData().getFoodLevel()),
				String.valueOf(round(this.playerEntity.getAttributeValue(Attributes.LUCK))) };
	}

	private static float round(double value) {
		return (float) Math.round(value * 100.0D) / 100.0F;
	}

	private float getDamage() {
		float damage = 0.0F;
		Item item = this.playerEntity.getMainHandItem().getItem();
		ArrayList<Object> levelList = LevelLists.customItemList;
		if (!levelList.isEmpty() && PlayerStatsManager.playerLevelisHighEnough(this.playerEntity, levelList, BuiltInRegistries.ITEM.getKey(item).toString(), false)) {
			if (item instanceof SwordItem swordItem) {
				damage = swordItem.getDamage();
			} else if (item instanceof DiggerItem miningToolItem) {
				damage = miningToolItem.getAttackDamage();
			} else if (!item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty() && item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE)) {
				Multimap<Attribute, AttributeModifier> multimap = item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
				for (Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
					if (entry.getKey().equals(Attributes.ATTACK_DAMAGE)) {
						damage = (float) entry.getValue().getAmount();
						break;
					}
				}
			}
		} else if (item instanceof TieredItem toolItem) {
			levelList = null;
			if (item instanceof SwordItem) {
				levelList = LevelLists.swordList;
			} else if (item instanceof AxeItem) {
				if (ConfigInit.CONFIG.bindAxeDamageToSwordRestriction) {
					levelList = LevelLists.swordList;
				} else {
					levelList = LevelLists.axeList;
				}
			} else if (item instanceof HoeItem) {
				levelList = LevelLists.hoeList;
			} else if (item instanceof PickaxeItem || item instanceof ShovelItem) {
				levelList = LevelLists.toolList;
			}
			if (levelList != null) {
				if (PlayerStatsManager.playerLevelisHighEnough(this.playerEntity, levelList, toolItem.getTier().toString().toLowerCase(), false)) {
					if (item instanceof SwordItem swordItem) {
						damage = swordItem.getDamage();
					} else if (item instanceof DiggerItem miningToolItem) {
						damage = miningToolItem.getAttackDamage();
					}
				}
			}
		}
		damage = (float) (damage + this.playerEntity.getAttributeValue(Attributes.ATTACK_DAMAGE));
		return damage;
	}

	public static class WidgetButtonPage extends Button {
		private final boolean hoverOutline;
		private final boolean clickable;
		private final int textureX;
		private final int textureY;
		private List<Component> tooltip = new ArrayList<Component>();
		private int clickedKey = -1;

		public WidgetButtonPage(int x, int y, int sizeX, int sizeY, int textureX, int textureY, boolean hoverOutline, boolean clickable, @Nullable Component tooltip, Button.OnPress onPress) {
			super(x, y, sizeX, sizeY, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
			this.hoverOutline = hoverOutline;
			this.clickable = clickable;
			this.textureX = textureX;
			this.textureY = textureY;
			this.width = sizeX;
			this.height = sizeY;
			if (tooltip != null) {
				this.tooltip.add(tooltip);
			}
		}

		@Override
		public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
			context.setColor(1.0f, 1.0f, 1.0f, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			int i = this.hoverOutline ? this.getTextureY() : 0;
			context.blit(ICON_TEXTURE, this.getX(), this.getY(), this.textureX + i * this.width, this.textureY, this.width, this.height);
			context.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			this.clickedKey = button;
			if (!this.clickable) {
				return false;
			}
			return super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		protected boolean isValidClickButton(int button) {
			return super.isValidClickButton(button) || button == 1 || button == 2;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (!this.clickable) {
				return false;
			}
			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		public void addTooltip(Component text) {
			this.tooltip.add(text);
		}

		public List<Component> getTooltipLines() {
			return this.tooltip;
		}

		public boolean wasMiddleButtonClicked() {
			return clickedKey == 2;
		}

		public boolean wasRightButtonClicked() {
			return clickedKey == 1;
		}

		private int getTextureY() {
			int i = 1;
			if (!this.active) {
				i = 0;
			} else if (this.isHovered()) {
				i = 2;
			}
			return i;
		}
	}
}
