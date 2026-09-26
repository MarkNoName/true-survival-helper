package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;

@Mixin(TridentItem.class)
public class TridentItemMixin {
	@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;startUsingItem(Lnet/minecraft/world/InteractionHand;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void useMixin(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info, ItemStack itemStack) {
		ArrayList<Object> levelList = LevelLists.customItemList;
		String string = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
		if (!levelList.isEmpty() && levelList.contains(string)) {
			if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, string, true)) {
				user.displayClientMessage(Component.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).withStyle(ChatFormatting.RED),
						true);
				info.setReturnValue(InteractionResultHolder.fail(itemStack));
			}
		} else {
			levelList = LevelLists.tridentList;
			if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, null, true)) {
				user.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
				info.setReturnValue(InteractionResultHolder.fail(itemStack));
			}
		}
	}
}
