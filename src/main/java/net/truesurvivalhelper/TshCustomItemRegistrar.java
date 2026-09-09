package net.truesurvivalhelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.levelz.data.LevelLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Registers this mod's canteen and spear items into LevelZ's {@code customItemList} so
 * LevelZ's own tooltip renderer and per-skill "Skill Info" screen (both of which read
 * that list generically, regardless of item type) pick them up natively.
 *
 * customItemList/customBlockList membership is NOT purely cosmetic, despite looking
 * that way from the tooltip code alone: LevelZ's own EventInit registers a generic
 * UseItemCallback/UseBlockCallback that enforces any list member itself (sends the
 * denial message, then returns a failing result) - and Fabric fires that callback
 * *before* the target's own use()/canActivate() ever runs, so a member on the list
 * never reaches this mod's dedicated Mixin at all.
 *
 * For canteens/spears that's harmless: their own Mixins would have produced the exact
 * same FAIL-and-message outcome, and drinking/throwing was never expected to swing the
 * arm specially. Water purifier, thermoregulator and the enchanted totem are
 * deliberately NOT registered into customItemList/customBlockList because for them it
 * isn't harmless - LevelZ's generic block callback denies with
 * ActionResult.success(false) (= InteractionResult.CONSUME), which never swings the arm
 * (only SUCCESS does), breaking the "swing-then-message, exactly like the furnace"
 * requirement; and its generic item callback fires on every plain right-click of the
 * totem regardless of Taniwha's death-protection context, breaking the "stay completely
 * silent, exactly like vanilla" requirement. Those three keep working only through
 * WaterPurifierBlockMixin, ThermoregulatorBlockMixin and EnchantedTotemItemMixin -
 * which requires their block/item IDs to stay off of LevelZ's own
 * customBlockList/customItemList entirely.
 *
 * They still show up in LevelZ's "Skill Info" screen though, via {@link #displayOnlyList}
 * - a separate, TSH-owned list in the same 5-tuple shape, spliced into
 * LevelLists.listOfAllLists by LevelLoaderMixin instead of into customItemList/
 * customBlockList. listOfAllLists is what SkillScrollableWidget (the screen's renderer)
 * actually reads, and it's a bag of *references* to whichever lists get added to it -
 * nothing about being in that bag requires also being in customItemList/customBlockList,
 * which is what the actual UseItemCallback/UseBlockCallback enforcement keys off of.
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

	/** Read by {@code LevelLoaderMixin} - see the class doc for why this list exists. */
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
}
