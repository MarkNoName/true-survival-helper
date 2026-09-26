package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.context.UseOnContext;

@Mixin(CompassItem.class)
public class CompassItemMixin {
	@Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"), cancellable = true)
	private void useOnBlockMixin(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
		if (context.getPlayer() != null) {
			ArrayList<Object> levelList = LevelLists.compassList;
			if (!PlayerStatsManager.playerLevelisHighEnough(context.getPlayer(), levelList, null, true)) {
				context.getPlayer().displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
				info.setReturnValue(InteractionResult.FAIL);
			}
		}
	}
}
