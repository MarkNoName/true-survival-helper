package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SwordItem.class)
public class SwordItemMixin {
	@Inject(method = "hurtEnemy", at = @At("HEAD"), cancellable = true)
	private void postHitMixin(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> info) {
		if (attacker instanceof Player playerEntity) {
			ArrayList<Object> levelList = LevelLists.customItemList;
			if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
				if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), true))
					info.setReturnValue(false);
			} else {
				levelList = LevelLists.swordList;
				if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((SwordItem) stack.getItem()).getTier().toString().toLowerCase(), true))
					info.setReturnValue(false);
			}
		}
	}

	@Inject(method = "mineBlock", at = @At("HEAD"), cancellable = true)
	private void postMineMixin(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> info) {
		if (miner instanceof Player playerEntity) {
			ArrayList<Object> levelList = LevelLists.customItemList;
			if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
				if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), true))
					info.setReturnValue(false);
			} else {
				levelList = LevelLists.swordList;
				if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((SwordItem) stack.getItem()).getTier().toString().toLowerCase(), true))
					info.setReturnValue(false);
			}
		}
	}
}
