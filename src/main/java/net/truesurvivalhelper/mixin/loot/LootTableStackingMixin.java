package net.truesurvivalhelper.mixin.loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(LootTable.class)
public abstract class LootTableStackingMixin {
	@Unique
	private List<ItemStack> tsh$preservedXpBottleStacks;

	@Inject(method = "shuffleAndSplitItems", at = @At("HEAD"))
	private void tsh$preserveXpBottleStacks(ObjectArrayList<ItemStack> items, int slots, RandomSource random, CallbackInfo ci) {
		List<ItemStack> preserved = new ArrayList<>();
		Iterator<ItemStack> iterator = items.iterator();
		while (iterator.hasNext()) {
			ItemStack stack = iterator.next();
			if (stack.is(Items.EXPERIENCE_BOTTLE) && stack.getCount() > 1) {
				preserved.add(stack);
				iterator.remove();
			}
		}
		this.tsh$preservedXpBottleStacks = preserved;
	}

	@Inject(method = "shuffleAndSplitItems", at = @At("RETURN"))
	private void tsh$restoreXpBottleStacks(ObjectArrayList<ItemStack> items, int slots, RandomSource random, CallbackInfo ci) {
		items.addAll(this.tsh$preservedXpBottleStacks);
		this.tsh$preservedXpBottleStacks = null;
	}
}
