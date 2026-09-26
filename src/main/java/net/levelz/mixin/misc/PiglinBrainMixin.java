package net.levelz.mixin.misc;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(PiglinAi.class)
public class PiglinBrainMixin {
	@Inject(method = "throwItemsTowardPlayer(Lnet/minecraft/world/entity/monster/piglin/Piglin;Lnet/minecraft/world/entity/player/Player;Ljava/util/List;)V", at = @At("HEAD"), cancellable = true)
	private static void dropBarteredItemMixin(Piglin piglin, Player player, List<ItemStack> items, CallbackInfo info) {
		ArrayList<Object> levelList = LevelLists.piglinList;
		if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
			player.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			if (!items.isEmpty()) {
				piglin.swing(InteractionHand.OFF_HAND);
				BehaviorUtils.throwItem(piglin, new ItemStack(Items.GOLD_INGOT), player.position().add(0.0, 1.0, 0.0));
			}
			info.cancel();
		}
	}

	@Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;split(I)Lnet/minecraft/world/item/ItemStack;", shift = Shift.BEFORE), cancellable = true)
	private static void playerInteractMixin(Piglin piglin, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		ArrayList<Object> levelList = LevelLists.piglinList;
		if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
			player.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			info.setReturnValue(InteractionResult.FAIL);
		}
	}
}
