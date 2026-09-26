package net.truesurvivalhelper;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.truesurvivalhelper.mixin.loot.LootTableBuilderAccessor;

public final class TshXpBottleLoot {
	private TshXpBottleLoot() {
	}

	public static void register() {
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (((LootTableBuilderAccessor) tableBuilder).tsh$paramSet() != LootContextParamSets.CHEST) {
				return;
			}
			addOverworldPools(tableBuilder);
			addNetherPools(tableBuilder);
			addEndPools(tableBuilder);
		});
	}

	private static void addOverworldPools(LootTable.Builder table) {
		float outer = 0.80F;
		table.withPool(pool(Level.OVERWORLD, outer, 0.35F, fixed(1), fixed(1)));
		table.withPool(pool(Level.OVERWORLD, outer, 0.28F, range(1, 2), range(1, 2)));
		table.withPool(pool(Level.OVERWORLD, outer, 0.12F, fixed(1), range(2, 3)));
		table.withPool(pool(Level.OVERWORLD, outer, 0.06F, fixed(1), range(2, 3)));
		table.withPool(pool(Level.OVERWORLD, outer, 0.03F, fixed(1), range(3, 5)));
	}

	private static void addNetherPools(LootTable.Builder table) {
		float outer = 0.90F;
		table.withPool(pool(Level.NETHER, outer, 1.0F, range(2, 4), range(1, 2)));
		table.withPool(pool(Level.NETHER, outer, 0.35F, range(1, 2), range(2, 3)));
		table.withPool(pool(Level.NETHER, outer, 0.15F, fixed(1), range(4, 6)));
	}

	private static void addEndPools(LootTable.Builder table) {
		float outer = 1.0F;
		table.withPool(pool(Level.END, outer, 1.0F, range(3, 5), range(1, 2)));
		table.withPool(pool(Level.END, outer, 0.40F, range(1, 3), range(2, 4)));
		table.withPool(pool(Level.END, outer, 0.25F, range(1, 2), range(4, 7)));
		table.withPool(pool(Level.END, outer, 0.10F, fixed(1), range(6, 12)));
	}

	private static LootPool.Builder pool(ResourceKey<Level> dimension, float outerChance, float poolChance, NumberProvider rolls, NumberProvider count) {
		LootPool.Builder pool = LootPool.lootPool()
				.setRolls(rolls)
				.add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).apply(SetItemCountFunction.setCount(count)))
				.when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setDimension(dimension)));
		if (outerChance < 1.0F) {
			pool.when(LootItemRandomChanceCondition.randomChance(outerChance));
		}
		if (poolChance < 1.0F) {
			pool.when(LootItemRandomChanceCondition.randomChance(poolChance));
		}
		return pool;
	}

	private static NumberProvider fixed(int value) {
		return ConstantValue.exactly(value);
	}

	private static NumberProvider range(float min, float max) {
		return UniformGenerator.between(min, max);
	}
}
