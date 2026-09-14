package net.truesurvivalhelper.mixin.tan;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import toughasnails.item.EmptyCanteenItem;

@Mixin(EmptyCanteenItem.class)
public interface EmptyCanteenItemAccessor {
	@Accessor("tier")
	int tsh$getTier();
}
