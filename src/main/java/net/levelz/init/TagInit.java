package net.levelz.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TagInit {
	public static final TagKey<Item> FARM_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation("levelz", "farm_items"));
	public static final TagKey<Item> RESTRICTED_FURNACE_EXPERIENCE_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation("levelz", "restricted_furnace_experience_items"));

	public static void init() {
	}
}
