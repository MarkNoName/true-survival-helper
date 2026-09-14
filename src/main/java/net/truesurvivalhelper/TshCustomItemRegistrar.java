package net.truesurvivalhelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.levelz.data.LevelLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class TshCustomItemRegistrar implements SimpleSynchronousResourceReloadListener {
	private static final String CANTEEN_SKILL = "stamina";
	private static final String FARMING_SKILL = "farming";
	private static final String ALCHEMY_SKILL = "alchemy";
	private static final String AGILITY_SKILL = "agility";

	private static final String[] SPEAR_TIERS = {"stone", "copper", "iron", "golden", "diamond", "netherite"};
	private static final String[] SPEAR_MATERIAL_KEYS = {"stone", "copper", "iron", "gold", "diamond", "netherite"};

	private static final ArrayList<Object> displayOnlyList = new ArrayList<>();

	public static ArrayList<Object> getDisplayOnlyList() {
		return displayOnlyList;
	}

	@Override
	public ResourceLocation getFabricId() {
		return new ResourceLocation("tsh", "custom_item_registrar");
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return List.of(new ResourceLocation("levelz", "level_loader"));
	}

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		registerCanteens();
		registerSpears();
		registerDisplayOnly();
	}

	private void registerDisplayOnly() {
		displayOnlyList.clear();
		addDisplayEntry("toughasnails:water_purifier", FARMING_SKILL, 1, "minecraft:custom_block");
		addDisplayEntry("toughasnails:thermoregulator", FARMING_SKILL, 3, "minecraft:custom_block");
		addDisplayEntry("biomemakeover:enchanted_totem", ALCHEMY_SKILL, 12, "minecraft:custom_item");
	}

	private void addDisplayEntry(String id, String skill, int level, String category) {
		displayOnlyList.add(id);
		displayOnlyList.add(skill);
		displayOnlyList.add(level);
		displayOnlyList.add(category);
		displayOnlyList.add(false);
	}

	private void registerCanteens() {
		String[] tiers = MaterialLevels.canteenTierMaterials();
		for (int i = 0; i < tiers.length; i++) {
			int level = MaterialLevels.forCanteenTier(i);
			if (level <= 0) {
				continue;
			}
			registerItem("tsh:" + tiers[i] + "_canteen", CANTEEN_SKILL, level);
		}
	}

	private void registerSpears() {
		for (int i = 0; i < SPEAR_TIERS.length; i++) {
			int level = MaterialLevels.forMaterial(SPEAR_MATERIAL_KEYS[i]);
			if (level <= 0) {
				continue;
			}
			registerItem("minecraft:" + SPEAR_TIERS[i] + "_spear", AGILITY_SKILL, level);
		}
	}

	private void registerItem(String itemId, String skill, int level) {
		LevelLists.customItemList.add(itemId);
		LevelLists.customItemList.add(skill);
		LevelLists.customItemList.add(level);
		LevelLists.customItemList.add("minecraft:custom_item");
		LevelLists.customItemList.add(false);
	}
}
