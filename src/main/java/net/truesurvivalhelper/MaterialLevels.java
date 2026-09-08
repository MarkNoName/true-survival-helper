package net.truesurvivalhelper;

import java.util.Locale;
import java.util.Map;

/**
 * Single source of truth for the pack's material tier -> required skill level table,
 * mirroring the values already baked into the pack's LevelZ/KubeJS datapack for vanilla
 * materials (leather/stone/gold/iron/diamond/netherite). Reused here for every
 * tiered item from a third-party mod (canteens, spears, ...) so a whole material tier
 * is described once instead of once per concrete item/variant.
 */
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

	// Matches ToughAsNails' EmptyCanteenItem#tier ordering: 0=leather 1=copper 2=iron 3=gold 4=diamond 5=netherite
	private static final String[] CANTEEN_TIER_MATERIAL = {"leather", "copper", "iron", "gold", "diamond", "netherite"};

	/**
	 * @return the required level for a material name (case-insensitive), or 0 (unrestricted)
	 * if the material has no entry (e.g. "wood").
	 */
	public static int forMaterial(String material) {
		if (material == null) {
			return 0;
		}
		return LEVEL_BY_MATERIAL.getOrDefault(material.toLowerCase(Locale.ROOT), 0);
	}

	/**
	 * @return the required level for a ToughAsNails canteen tier index, or 0 if out of range.
	 */
	public static int forCanteenTier(int tier) {
		if (tier < 0 || tier >= CANTEEN_TIER_MATERIAL.length) {
			return 0;
		}
		return forMaterial(CANTEEN_TIER_MATERIAL[tier]);
	}

	/**
	 * @return the canteen tier material names, in ToughAsNails' own tier-index order
	 * (0=leather ... 5=netherite), for anything that needs to enumerate every tier.
	 */
	public static String[] canteenTierMaterials() {
		return CANTEEN_TIER_MATERIAL.clone();
	}
}
