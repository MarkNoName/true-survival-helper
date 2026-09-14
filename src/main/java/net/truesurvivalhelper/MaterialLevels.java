package net.truesurvivalhelper;

import java.util.Locale;
import java.util.Map;

public final class MaterialLevels {
	private MaterialLevels() {
	}

	private static final Map<String, Integer> LEVEL_BY_MATERIAL = Map.of(
			"leather", 1,
			"stone", 2,
			"copper", 3,
			"gold", 5,
			"iron", 6,
			"diamond", 10,
			"netherite", 14
	);

	private static final String[] CANTEEN_TIER_MATERIAL = {"leather", "copper", "iron", "gold", "diamond", "netherite"};

	public static int forMaterial(String material) {
		if (material == null) {
			return 0;
		}
		return LEVEL_BY_MATERIAL.getOrDefault(material.toLowerCase(Locale.ROOT), 0);
	}

	public static int forCanteenTier(int tier) {
		if (tier < 0 || tier >= CANTEEN_TIER_MATERIAL.length) {
			return 0;
		}
		return forMaterial(CANTEEN_TIER_MATERIAL[tier]);
	}

	public static String[] canteenTierMaterials() {
		return CANTEEN_TIER_MATERIAL.clone();
	}
}
