package net.levelz.mixin.entity;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

@Mixin(Axolotl.class)
public class AxolotlEntityMixin {
	@Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
	private void interactMobMixin(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		if (player.getItemInHand(hand).getItem() == Items.WATER_BUCKET) {
			ArrayList<Object> levelList = LevelLists.axolotlList;
			if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
				player.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
				info.setReturnValue(InteractionResult.FAIL);
			}
		}
	}
}
