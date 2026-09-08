package net.truesurvivalhelper.mixin.tan;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import toughasnails.item.EmptyCanteenItem;

/**
 * Exposes EmptyCanteenItem's package-private {@code tier} field to the rest of this
 * mod (used by ItemTooltipMixin, which mixes into vanilla ItemStack rather than the
 * canteen classes themselves, so it can't just @Shadow the field like the item-side
 * Mixins do). Applies to FilledCanteenItem too since it's a subclass.
 */
@Mixin(EmptyCanteenItem.class)
public interface EmptyCanteenItemAccessor {
	@Accessor("tier")
	int tsh$getTier();
}
