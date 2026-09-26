package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.context.UseOnContext;

@Mixin(FlintAndSteelItem.class)
public class FlintAndSteelItemMixin {
	@Inject(method = "useOn", at = @At(value = "HEAD"), cancellable = true)
	private void useOnBlockMixin(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
		ArrayList<Object> levelList = LevelLists.flintAndSteelList;
		if (!PlayerStatsManager.playerLevelisHighEnough(context.getPlayer(), levelList, null, true)) {
			context.getPlayer().displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			info.setReturnValue(InteractionResult.FAIL);
		}
	}
}
