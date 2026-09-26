package net.levelz.mixin.item;

import java.util.ArrayList;
import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(DiggerItem.class)
public class MiningToolItemMixin {
	@Inject(method = "hurtEnemy", at = @At("HEAD"), cancellable = true)
	private void postHitMixin(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> info) {
		if (attacker instanceof Player player && stack.getItem() instanceof AxeItem axeItem && ConfigInit.CONFIG.bindAxeDamageToSwordRestriction
				&& PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.swordList, axeItem.getTier().toString().toLowerCase(Locale.ROOT), true)) {
			stack.hurtAndBreak(2, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			info.setReturnValue(true);
			return;
		}
		if (attacker instanceof Player) {
			ArrayList<Object> levelList = LevelLists.customItemList;
			if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
				if (!PlayerStatsManager.playerLevelisHighEnough((Player) attacker, levelList, BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), true))
					info.setReturnValue(false);
			} else {
				levelList = null;
				Item item = stack.getItem();
				if (item instanceof AxeItem)
					levelList = LevelLists.axeList;
				else if (item instanceof HoeItem)
					levelList = LevelLists.hoeList;
				else if (item instanceof PickaxeItem || item instanceof ShovelItem)
					levelList = LevelLists.toolList;
				if (levelList != null)
					if (!PlayerStatsManager.playerLevelisHighEnough((Player) attacker, levelList, ((DiggerItem) item).getTier().toString().toLowerCase(), true))
						info.setReturnValue(false);
			}
		}
	}

	@Inject(method = "mineBlock", at = @At("HEAD"), cancellable = true)
	private void postMineMixin(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> info) {
		if (miner instanceof Player) {
			ArrayList<Object> levelList = LevelLists.customItemList;
			if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
				if (!PlayerStatsManager.playerLevelisHighEnough((Player) miner, levelList, BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), true))
					info.setReturnValue(false);
			} else {
				levelList = null;
				Item item = stack.getItem();
				if (item instanceof AxeItem)
					levelList = LevelLists.axeList;
				else if (item instanceof HoeItem)
					levelList = LevelLists.hoeList;
				else if (item instanceof PickaxeItem || item instanceof ShovelItem)
					levelList = LevelLists.toolList;
				if (levelList != null)
					if (!PlayerStatsManager.playerLevelisHighEnough((Player) miner, levelList, ((DiggerItem) item).getTier().toString().toLowerCase(), true))
						info.setReturnValue(false);
			}
		}
	}
}
