package net.levelz.mixin.misc;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(AbstractContainerMenu.class)
public class ScreenHandlerMixin {
	@Shadow
	private ItemStack carried;

	@Nullable
	private ContainerSynchronizer syncHandler;

	@Nullable
	@Shadow
	@Final
	private MenuType<?> menuType;

	@Shadow
	@Final
	@Mutable
	public NonNullList<Slot> slots = NonNullList.create();

	@Inject(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameTags(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 0), cancellable = true)
	private void internalOnSlotClickNewMixin(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo info) {
		if (8 - Mob.getEquipmentSlotForItem(carried).getIndex() == slotIndex
				&& (this.slots.get(slotIndex).toString().contains("PlayerScreenHandler") || this.slots.get(slotIndex).toString().contains("class_1723"))) {
			if (carried.getItem() instanceof ArmorItem armorItem) {
				ArrayList<Object> levelList = LevelLists.customItemList;
				try {
					if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(armorItem).toString())) {
						if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(armorItem).toString(), true))
							info.cancel();
					} else {
						levelList = LevelLists.armorList;
						if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
							info.cancel();
					}
				} catch (AbstractMethodError ignore) {
				}
			} else if (carried.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
				info.cancel();
			} else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(BuiltInRegistries.ITEM.getKey(carried.getItem()).toString())
					&& !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(carried.getItem()).toString(), true))
				info.cancel();
		} else if (menuType == MenuType.BREWING_STAND && slotIndex == 3 && !carried.isEmpty()) {
			if (PlayerStatsManager.listContainsItemOrBlock(player, BuiltInRegistries.ITEM.getId(carried.getItem()), 2) && !player.isCreative()) {
				info.cancel();
			}
		}
	}

	@Inject(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 2, shift = Shift.BEFORE), cancellable = true)
	private void internalOnSlotClickMixin(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo info) {
		if (8 - Mob.getEquipmentSlotForItem(carried).getIndex() == slotIndex
				&& (this.slots.get(slotIndex).toString().contains("PlayerScreenHandler") || this.slots.get(slotIndex).toString().contains("class_1723"))
				&& this.slots.get(slotIndex).mayPlace(carried)) {
			if (carried.getItem() instanceof ArmorItem armorItem) {
				ArrayList<Object> levelList = LevelLists.customItemList;
				try {
					if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(armorItem).toString())) {
						if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(armorItem).toString(), true))
							info.cancel();
					} else {
						levelList = LevelLists.armorList;
						if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
							info.cancel();
					}
				} catch (AbstractMethodError ignore) {
				}
			} else if (carried.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
				info.cancel();
			} else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(BuiltInRegistries.ITEM.getKey(carried.getItem()).toString())
					&& !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(carried.getItem()).toString(), true))
				info.cancel();
		} else if (menuType == MenuType.BREWING_STAND && slotIndex == 3 && !carried.isEmpty()) {
			if (PlayerStatsManager.listContainsItemOrBlock(player, BuiltInRegistries.ITEM.getId(carried.getItem()), 2) && !player.isCreative()) {
				info.cancel();
			}
		}
	}

	@Inject(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;setByPlayer(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 1, shift = Shift.BEFORE), cancellable = true)
	private void internalOnSlotClickSwitchMixin(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo info) {
		if (8 - Mob.getEquipmentSlotForItem(carried).getIndex() == slotIndex
				&& (this.slots.get(slotIndex).toString().contains("PlayerScreenHandler") || this.slots.get(slotIndex).toString().contains("class_1723"))) {
			if (carried.getItem() instanceof ArmorItem armorItem) {
				ArrayList<Object> levelList = LevelLists.customItemList;
				try {
					if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(armorItem).toString())) {
						if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(armorItem).toString(), true))
							info.cancel();
					} else {
						levelList = LevelLists.armorList;
						if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
							info.cancel();
					}
				} catch (AbstractMethodError ignore) {
				}
			} else if (carried.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
				info.cancel();
			} else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(BuiltInRegistries.ITEM.getKey(carried.getItem()).toString())
					&& !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(carried.getItem()).toString(), true))
				info.cancel();
		} else if (menuType == MenuType.BREWING_STAND && slotIndex == 3 && !carried.isEmpty()) {
			if (PlayerStatsManager.listContainsItemOrBlock(player, BuiltInRegistries.ITEM.getId(carried.getItem()), 2) && !player.isCreative()) {
				info.cancel();
			}
		}
	}

	@Inject(method = "doClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/inventory/Slot;getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I", ordinal = 2), cancellable = true)
	private void internalOnSlotSwitchSetMixin(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo info) {
		Inventory playerInventory = player.getInventory();
		ItemStack itemStack = playerInventory.getItem(button);
		if (8 - Mob.getEquipmentSlotForItem(itemStack).getIndex() == slotIndex
				&& (this.slots.get(slotIndex).toString().contains("PlayerScreenHandler") || this.slots.get(slotIndex).toString().contains("class_1723"))) {
			if (itemStack.getItem() instanceof ArmorItem armorItem) {
				ArrayList<Object> levelList = LevelLists.customItemList;
				try {
					if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(armorItem).toString())) {
						if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(armorItem).toString(), true))
							info.cancel();
					} else {
						levelList = LevelLists.armorList;
						if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
							info.cancel();
					}
				} catch (AbstractMethodError ignore) {
				}
			} else if (itemStack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
				info.cancel();
			} else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString())
					&& !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString(), true))
				info.cancel();
		} else if (menuType == MenuType.BREWING_STAND && slotIndex == 3 && !itemStack.isEmpty()) {
			if (PlayerStatsManager.listContainsItemOrBlock(player, BuiltInRegistries.ITEM.getId(itemStack.getItem()), 2) && !player.isCreative()) {
				info.cancel();
			}
		}
	}

	@Inject(method = "doClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/inventory/Slot;getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I", ordinal = 3), cancellable = true)
	private void internalOnSlotSwitchSwitchMixin(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo info) {
		Inventory playerInventory = player.getInventory();
		ItemStack itemStack = playerInventory.getItem(button);
		if (8 - Mob.getEquipmentSlotForItem(itemStack).getIndex() == slotIndex
				&& (this.slots.get(slotIndex).toString().contains("PlayerScreenHandler") || this.slots.get(slotIndex).toString().contains("class_1723"))) {
			if (itemStack.getItem() instanceof ArmorItem armorItem) {
				ArrayList<Object> levelList = LevelLists.customItemList;
				try {
					if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(armorItem).toString())) {
						if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(armorItem).toString(), true))
							info.cancel();
					} else {
						levelList = LevelLists.armorList;
						if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
							info.cancel();
					}
				} catch (AbstractMethodError ignore) {
				}
			} else if (itemStack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
				info.cancel();
			} else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString())
					&& !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString(), true))
				info.cancel();
		} else if (menuType == MenuType.BREWING_STAND && slotIndex == 3 && !itemStack.isEmpty()) {
			if (PlayerStatsManager.listContainsItemOrBlock(player, BuiltInRegistries.ITEM.getId(itemStack.getItem()), 2) && !player.isCreative()) {
				info.cancel();
			}
		}
	}

	@Inject(method = "doClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/core/NonNullList;get(I)Ljava/lang/Object;", ordinal = 1), cancellable = true)
	private void internalOnSlotClickQuickMixin(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo info) {
		ItemStack itemStack = this.slots.get(slotIndex).getItem();
		if (itemStack.getItem() instanceof ArmorItem armorItem) {
			ArrayList<Object> levelList = LevelLists.customItemList;
			try {
				if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(armorItem).toString())) {
					if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(armorItem).toString(), true))
						info.cancel();
				} else {
					levelList = LevelLists.armorList;
					if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, armorItem.getMaterial().getName().toLowerCase(), true))
						info.cancel();
				}
			} catch (AbstractMethodError ignore) {
			}
		} else if (itemStack.getItem() == Items.ELYTRA && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.elytraList, null, true)) {
			info.cancel();
		} else if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString())
				&& !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString(), true))
			info.cancel();
	}
}
