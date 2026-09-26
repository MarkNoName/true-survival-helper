package net.levelz.mixin.misc;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.Skill;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {
	@Shadow
	@Final
	private DataSlot cost;

	private int smithingLevel = ((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(Skill.SMITHING);

	public AnvilScreenHandlerMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context) {
		super(type, syncId, playerInventory, context);
	}

	@Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
	protected void canTakeOutputMixin(Player player, boolean present, CallbackInfoReturnable<Boolean> info) {
		if (cost.get() <= 0 && (smithingLevel >= ConfigInit.CONFIG.maxLevel || (int) (1F - smithingLevel * ConfigInit.CONFIG.smithingCostBonus) <= 0))
			info.setReturnValue(true);
	}

	@Inject(method = "createResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 4))
	private void updateResultMixin(CallbackInfo info) {
		if (this.cost.get() > 1) {
			int levelCost = (int) (this.cost.get() * (1F - smithingLevel * ConfigInit.CONFIG.smithingCostBonus));
			if (levelCost > 30 && smithingLevel >= ConfigInit.CONFIG.maxLevel) {
				this.cost.set(30);
			} else
				this.cost.set(levelCost < 0 ? 0 : levelCost);
		}
	}

	@Inject(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;get()I"), require = 0)
	private void onTakeOutputMixin(Player playerEntity, ItemStack stack, CallbackInfo ci) {
		if ((smithingLevel >= ConfigInit.CONFIG.maxLevel) && (ConfigInit.CONFIG.smithingAnvilChance > playerEntity.level().getRandom().nextFloat()))
			cost.set(0);
	}

	@Environment(EnvType.CLIENT)
	@Inject(method = "getCost", at = @At(value = "HEAD"), cancellable = true)
	public void getLevelCostMixin(CallbackInfoReturnable<Integer> info) {
		if (this.cost.get() > 30 && smithingLevel >= ConfigInit.CONFIG.maxLevel) {
			info.setReturnValue(30);
		}
	}
}
