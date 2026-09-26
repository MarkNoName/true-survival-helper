package net.levelz.screen.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.Block;

@Environment(EnvType.CLIENT)
public class SkillUnlocks {
	public static final int ITEM = 0;
	public static final int BLOCK = 1;
	public static final int ENTITY = 2;
	public static final int BREWING = 3;
	public static final int SMITHING = 4;

	public static final String[] CATEGORY_KEYS = { "text.levelz.gui.item_usage", "text.levelz.gui.block_usage", "text.levelz.gui.entity_usage", "text.levelz.gui.brewing", "text.levelz.gui.smithing" };

	private static int groupCounter = 0;

	private static final ResourceLocation DEFAULT_ENTITY_SPRITE = new ResourceLocation("levelz", "textures/gui/skills/sprites/entity/default.png");

	public record Unlock(int level, String skill, ItemStack stack, @Nullable ResourceLocation sprite, List<Component> names, int group) {
		public Component getName() {
			return this.names.get(0);
		}
	}

	public static Map<Integer, Map<Integer, List<Unlock>>> getSkillUnlocks(String skill) {
		Map<Integer, Map<Integer, List<Unlock>>> unlocks = new TreeMap<>();
		for (List<Object> list : LevelLists.listOfAllLists) {
			if (list.isEmpty()) {
				continue;
			}
			if (isSingleList(list)) {
				if (list.get(0).toString().equals(skill)) {
					addObject(unlocks, skill, (Integer) list.get(1), list.get(2).toString());
				}
				continue;
			}
			for (int i = 0; i + 4 < list.size(); i += 5) {
				if (!list.get(i + 1).toString().equals(skill) || !(list.get(i + 2) instanceof Integer level)) {
					continue;
				}
				String object = list.get(i).toString();
				String type = list.get(i + 3).toString();
				switch (type) {
				case "minecraft:custom_item" -> addItem(unlocks, skill, level, object);
				case "minecraft:custom_block" -> addBlock(unlocks, skill, level, object);
				case "minecraft:custom_entity" -> addEntity(unlocks, skill, level, object);
				default -> addMaterial(unlocks, skill, level, type, object);
				}
			}
		}
		switch (skill) {
		case "alchemy" -> addLevelList(unlocks, BREWING, getListUnlocks("alchemy"));
		case "smithing" -> addLevelList(unlocks, SMITHING, getListUnlocks("smithing"));
		default -> {
		}
		}
		return unlocks;
	}

	public static List<Unlock> getListUnlocks(String title) {
		List<Unlock> unlocks = new ArrayList<>();
		switch (title) {
		case "mining" -> addRawList(unlocks, "mining", LevelLists.miningLevelList, LevelLists.miningBlockList, null, true);
		case "alchemy" -> addRawList(unlocks, "alchemy", LevelLists.brewingLevelList, LevelLists.brewingItemList, null, false);
		case "smithing" -> addRawList(unlocks, "smithing", LevelLists.smithingLevelList, LevelLists.smithingItemList, null, false);
		case "crafting" -> addRawList(unlocks, null, LevelLists.craftingLevelList, LevelLists.craftingItemList, LevelLists.craftingSkillList, false);
		default -> {
		}
		}
		return unlocks;
	}

	private static int nextGroup() {
		return groupCounter++;
	}

	private static boolean isSingleList(List<Object> list) {
		return list.size() >= 4 && list.get(0) instanceof String && list.get(1) instanceof Integer && list.get(2) instanceof String && list.get(3) instanceof Boolean;
	}

	private static void addLevelList(Map<Integer, Map<Integer, List<Unlock>>> unlocks, int category, List<Unlock> list) {
		for (Unlock unlock : list) {
			add(unlocks, category, unlock);
		}
	}

	private static void addRawList(List<Unlock> unlocks, @Nullable String skill, List<Integer> levelList, List<List<Integer>> objectList, @Nullable List<String> skillList, boolean blocks) {
		for (int i = 0; i < levelList.size() && i < objectList.size(); i++) {
			String entrySkill = skillList != null && i < skillList.size() ? skillList.get(i) : skill;
			if (entrySkill == null) {
				continue;
			}
			for (int rawId : objectList.get(i)) {
				if (blocks) {
					Block block = BuiltInRegistries.BLOCK.byId(rawId);
					unlocks.add(new Unlock(levelList.get(i), entrySkill, new ItemStack(block), null, List.of(block.getName()), nextGroup()));
				} else {
					Item item = BuiltInRegistries.ITEM.byId(rawId);
					unlocks.add(new Unlock(levelList.get(i), entrySkill, item.getDefaultInstance(), null, getItemNames(item, "alchemy".equals(skill)), nextGroup()));
				}
			}
		}
	}

	private static List<Component> getItemNames(Item item, boolean brewing) {
		List<Component> names = new ArrayList<>();
		names.add(item.getDescription());
		if (brewing && PotionBrewing.isIngredient(item.getDefaultInstance()) && LevelLists.potionList.contains(item)) {
			int index = LevelLists.potionList.indexOf(item);
			if (index + 1 < LevelLists.potionList.size() && LevelLists.potionList.get(index + 1) instanceof Potion potion) {
				ItemStack potionStack = PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
				names.add(Component.nullToEmpty("Ingredient for " + Component.translatable(((PotionItem) potionStack.getItem()).getDescriptionId(potionStack)).getString()));
			}
		}
		return names;
	}

	private static void addObject(Map<Integer, Map<Integer, List<Unlock>>> unlocks, String skill, int level, String object) {
		ResourceLocation identifier = ResourceLocation.tryParse(object);
		if (identifier == null) {
			return;
		}
		if (BuiltInRegistries.BLOCK.containsKey(identifier)) {
			addBlock(unlocks, skill, level, object);
		} else if (BuiltInRegistries.ITEM.containsKey(identifier)) {
			addItem(unlocks, skill, level, object);
		} else if (BuiltInRegistries.ENTITY_TYPE.containsKey(identifier)) {
			addEntity(unlocks, skill, level, object);
		} else {
			add(unlocks, ENTITY, new Unlock(level, skill, ItemStack.EMPTY, DEFAULT_ENTITY_SPRITE, List.of(getObjectName(identifier)), nextGroup()));
		}
	}

	private static void addItem(Map<Integer, Map<Integer, List<Unlock>>> unlocks, String skill, int level, String object) {
		ResourceLocation identifier = ResourceLocation.tryParse(object);
		if (identifier == null) {
			return;
		}
		if (BuiltInRegistries.ITEM.containsKey(identifier)) {
			Item item = BuiltInRegistries.ITEM.get(identifier);
			add(unlocks, ITEM, new Unlock(level, skill, item.getDefaultInstance(), null, List.of(item.getDescription()), nextGroup()));
			return;
		}
		String iconKey = String.format("text.levelz.object_icon.%s", identifier.getPath());
		ResourceLocation icon = Language.getInstance().has(iconKey) ? ResourceLocation.tryParse(Language.getInstance().getOrDefault(iconKey)) : null;
		if (icon != null && BuiltInRegistries.ITEM.containsKey(icon)) {
			add(unlocks, ITEM, new Unlock(level, skill, BuiltInRegistries.ITEM.get(icon).getDefaultInstance(), null, List.of(getObjectName(identifier)), nextGroup()));
		} else {
			add(unlocks, ITEM, new Unlock(level, skill, ItemStack.EMPTY, DEFAULT_ENTITY_SPRITE, List.of(getObjectName(identifier)), nextGroup()));
		}
	}

	private static Component getObjectName(ResourceLocation identifier) {
		String translationKey = String.format("text.levelz.object_info.%s", identifier.getPath());
		return Language.getInstance().has(translationKey) ? Component.translatable(translationKey) : Component.nullToEmpty(StringUtils.capitalize(identifier.getPath().replace('_', ' ')));
	}

	private static void addBlock(Map<Integer, Map<Integer, List<Unlock>>> unlocks, String skill, int level, String object) {
		ResourceLocation identifier = ResourceLocation.tryParse(object);
		if (identifier == null || !BuiltInRegistries.BLOCK.containsKey(identifier)) {
			return;
		}
		Block block = BuiltInRegistries.BLOCK.get(identifier);
		add(unlocks, BLOCK, new Unlock(level, skill, new ItemStack(block), null, List.of(block.getName()), nextGroup()));
	}

	private static void addEntity(Map<Integer, Map<Integer, List<Unlock>>> unlocks, String skill, int level, String object) {
		ResourceLocation identifier = ResourceLocation.tryParse(object);
		if (identifier == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(identifier)) {
			return;
		}
		EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(identifier);
		ResourceLocation sprite = new ResourceLocation("levelz", "textures/gui/skills/sprites/entity/" + identifier.getPath() + ".png");
		if (Minecraft.getInstance().getResourceManager().getResource(sprite).isPresent()) {
			add(unlocks, ENTITY, new Unlock(level, skill, ItemStack.EMPTY, sprite, List.of(entityType.getDescription()), nextGroup()));
		} else if (SpawnEggItem.byId(entityType) != null) {
			add(unlocks, ENTITY, new Unlock(level, skill, new ItemStack(SpawnEggItem.byId(entityType)), null, List.of(entityType.getDescription()), nextGroup()));
		} else {
			add(unlocks, ENTITY, new Unlock(level, skill, ItemStack.EMPTY, DEFAULT_ENTITY_SPRITE, List.of(entityType.getDescription()), nextGroup()));
		}
	}

	private static void addMaterial(Map<Integer, Map<Integer, List<Unlock>>> unlocks, String skill, int level, String type, String material) {
		int group = nextGroup();
		for (Item item : BuiltInRegistries.ITEM) {
			if (material.equals(getMaterial(item, type))) {
				add(unlocks, ITEM, new Unlock(level, skill, item.getDefaultInstance(), null, List.of(item.getDescription()), group));
			}
		}
	}

	@Nullable
	private static String getMaterial(Item item, String type) {
		switch (type) {
		case "minecraft:sword":
			return item instanceof SwordItem ? ((TieredItem) item).getTier().toString().toLowerCase() : null;
		case "minecraft:axe":
			return item instanceof AxeItem ? ((TieredItem) item).getTier().toString().toLowerCase() : null;
		case "minecraft:hoe":
			return item instanceof HoeItem ? ((TieredItem) item).getTier().toString().toLowerCase() : null;
		case "minecraft:tool":
			return item instanceof PickaxeItem || item instanceof ShovelItem ? ((TieredItem) item).getTier().toString().toLowerCase() : null;
		case "minecraft:armor":
			if (item instanceof ArmorItem armorItem) {
				try {
					return armorItem.getMaterial().getName().toLowerCase();
				} catch (AbstractMethodError ignored) {
				}
			}
			return null;
		default:
			return null;
		}
	}

	private static void add(Map<Integer, Map<Integer, List<Unlock>>> unlocks, int category, Unlock unlock) {
		if (unlock.level() < 1 || unlock.level() > ConfigInit.CONFIG.maxLevel || unlock.stack().isEmpty() && unlock.sprite() == null) {
			return;
		}
		unlocks.computeIfAbsent(category, key -> new TreeMap<>()).computeIfAbsent(unlock.level(), key -> new ArrayList<>()).add(unlock);
	}
}
