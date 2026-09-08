package net.truesurvivalhelper;

import java.util.Collection;
import java.util.List;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.levelz.data.LevelLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Registers every item/block this mod restricts through its own Mixins into LevelZ's
 * {@code customItemList}/{@code customBlockList} too - purely so LevelZ's own tooltip
 * renderer and per-skill "Skill Info" screen (both of which read those lists generically,
 * regardless of item type) pick them up natively. This registers no enforcement of its
 * own: the actual blocking still happens in this mod's Mixins, since none of these items
 * are covered by any of LevelZ's own enforcement Mixins in the first place.
 *
 * Runs after LevelZ's own "levelz:level_loader" reload (which clears and repopulates
 * every LevelLists list from JSON on every reload), so these additions always survive.
 */
public class TshCustomItemRegistrar implements SimpleSynchronousResourceReloadListener {
	private static final String CANTEEN_SKILL = "stamina";
	private static final String FARMING_SKILL = "farming";
	private static final String ALCHEMY_SKILL = "alchemy";
	private static final String AGILITY_SKILL = "agility";

	// minecraft:sword-tier naming used by VanillaBackport's spears (wood excluded - unrestricted)
	private static final String[] SPEAR_TIERS = {"stone", "copper", "iron", "golden", "diamond", "netherite"};
	// MaterialLevels keys don't include the vanilla "golden" spelling - map tier name -> material key here
	private static final String[] SPEAR_MATERIAL_KEYS = {"stone", "copper", "iron", "gold", "diamond", "netherite"};

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
		registerBlock("toughasnails:water_purifier", FARMING_SKILL, 1);
		registerBlock("toughasnails:thermoregulator", FARMING_SKILL, 3);
		registerItem("biomemakeover:enchanted_totem", ALCHEMY_SKILL, 12);
		registerSpears();
	}

	/**
	 * One synthetic, non-real "tsh:&lt;tier&gt;_canteen" entry per tier, purely so LevelZ's
	 * per-skill "Skill Info" list shows a single clean "Leather Canteen" line per
	 * material (see the matching en_us.json entries) instead of one line per one of
	 * the 4 real fill-state items (empty/water/dirty/purified) that tier actually has.
	 * The real items are never registered here - their tooltip is handled directly by
	 * CanteenTooltipMixin, and the actual restriction by EmptyCanteenItemMixin /
	 * FilledCanteenItemMixin, neither of which needs this list at all.
	 */
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

	private void registerBlock(String blockId, String skill, int level) {
		LevelLists.customBlockList.add(blockId);
		LevelLists.customBlockList.add(skill);
		LevelLists.customBlockList.add(level);
		LevelLists.customBlockList.add("minecraft:custom_block");
		LevelLists.customBlockList.add(false);
	}
}
