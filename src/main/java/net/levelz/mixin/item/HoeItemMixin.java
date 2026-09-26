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
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;

@Mixin(HoeItem.class)
public class HoeItemMixin {
	@Inject(method = "useOn", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"), cancellable = true)
	private void useOnBlockMixin(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
		ArrayList<Object> levelList = LevelLists.hoeList;
		String material = ((DiggerItem) context.getItemInHand().getItem()).getTier().toString().toLowerCase();
		if (!PlayerStatsManager.playerLevelisHighEnough(context.getPlayer(), levelList, material, true)) {
			context.getPlayer()
					.displayClientMessage(Component.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
							.withStyle(ChatFormatting.RED), true);
			info.setReturnValue(InteractionResult.FAIL);
		}
	}
}
