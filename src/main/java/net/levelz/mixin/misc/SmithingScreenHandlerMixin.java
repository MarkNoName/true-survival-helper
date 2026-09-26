package net.levelz.mixin.misc;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.stats.PlayerStatsManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipe;

@Mixin(SmithingMenu.class)
public abstract class SmithingScreenHandlerMixin extends ItemCombinerMenu {
	@Nullable
	@Shadow
	private SmithingRecipe selectedRecipe;

	public SmithingScreenHandlerMixin(MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context) {
		super(type, syncId, playerInventory, context);
	}

	@Inject(method = "createResult", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	private void updateResultMixin(CallbackInfo info, List<SmithingRecipe> list) {
		if (PlayerStatsManager.listContainsItemOrBlock(player, BuiltInRegistries.ITEM.getId(list.get(0).getResultItem(this.player.level().registryAccess()).getItem()), 3) && !player.isCreative()) {
			this.selectedRecipe = null;
			this.resultSlots.setItem(0, ItemStack.EMPTY);
			info.cancel();
		}
	}
}
