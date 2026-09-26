package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentScreenHandlerMixin {
	@Shadow
	@Final
	private Container enchantSlots;
	@Shadow
	@Final
	public int[] costs;
	@Shadow
	@Final
	public int[] enchantClue;
	@Shadow
	@Final
	public int[] levelClue;

	private Inventory playerInventory;

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At(value = "TAIL"))
	private void initMixin(int syncId, Inventory playerInventory, ContainerLevelAccess context, CallbackInfo info) {
		this.playerInventory = playerInventory;
	}

	@Inject(method = "slotsChanged", at = @At(value = "TAIL"))
	private void onContentChangedMixin(Container inventory, CallbackInfo info) {
		if (inventory == this.enchantSlots && playerInventory != null && !playerInventory.player.isCreative()) {
			ItemStack itemStack = inventory.getItem(0);
			if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
				PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerInventory.player).getPlayerStatsManager();
				ArrayList<Object> enchantingTableList = LevelLists.enchantingTableList;
				if (enchantingTableList != null && !enchantingTableList.isEmpty()) {
					int playerAlchemyLevel = playerStatsManager.getSkillLevel(Skill.valueOf(enchantingTableList.get(0).toString().toUpperCase()));
					if (playerAlchemyLevel < ConfigInit.CONFIG.maxLevel) {
						if (playerAlchemyLevel < (int) enchantingTableList.get(4)) {
							for (int i = 0; i < 3; ++i) {
								this.costs[i] = 0;
								this.enchantClue[i] = -1;
								this.levelClue[i] = -1;
							}
						} else if (playerAlchemyLevel < (int) enchantingTableList.get(5)) {
							for (int i = 1; i < 3; ++i) {
								this.costs[i] = 0;
								this.enchantClue[i] = -1;
								this.levelClue[i] = -1;
							}
						} else if (playerAlchemyLevel < (int) enchantingTableList.get(6)) {
							this.costs[2] = 0;
							this.enchantClue[2] = -1;
							this.levelClue[2] = -1;
						}
					}
				}
			}
		}
	}
}
