package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.stats.PlayerStatsManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Mixin(BrewingStandMenu.class)
public class BrewingStandScreenHandlerMixin {
	@Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 1, shift = Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	private void transferSlotMixin(Player player, int index, CallbackInfoReturnable<ItemStack> info, ItemStack itemStack, Slot slot, ItemStack itemStack2) {
		if (PlayerStatsManager.listContainsItemOrBlock(player, BuiltInRegistries.ITEM.getId(itemStack2.getItem()), 2) && !player.isCreative()) {
			info.setReturnValue(ItemStack.EMPTY);
		}
	}
}
