package net.levelz.mixin.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemNameBlockItem.class)
public abstract class AliasedBlockItemMixin extends BlockItem {
	public AliasedBlockItemMixin(Block block, Properties settings) {
		super(block, settings);
	}
}
