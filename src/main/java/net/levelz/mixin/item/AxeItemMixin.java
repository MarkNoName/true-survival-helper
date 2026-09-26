package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;

@Mixin(AxeItem.class)
public class AxeItemMixin {
	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	private void useOnBlockMixin(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
		Player playerEntity = context.getPlayer();
		ArrayList<Object> levelList;

		levelList = LevelLists.customItemList;
		if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(context.getItemInHand().getItem()).toString())) {
			if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, BuiltInRegistries.ITEM.getKey(context.getItemInHand().getItem()).toString(), true))
				info.setReturnValue(InteractionResult.PASS);
		} else {
			String material = ((AxeItem) context.getItemInHand().getItem()).getTier().toString().toLowerCase();
			levelList = LevelLists.axeList;
			if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, material, true)) {
				playerEntity.displayClientMessage(
						Component.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
								.withStyle(ChatFormatting.RED),
						true);
				info.setReturnValue(InteractionResult.PASS);
			}
		}
	}
}
