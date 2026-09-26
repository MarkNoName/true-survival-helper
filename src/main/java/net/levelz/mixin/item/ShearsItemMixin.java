package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {
	@Inject(method = "mineBlock", at = @At("HEAD"), cancellable = true)
	private void postMineMixin(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> info) {
		if (miner instanceof Player) {
			if (!PlayerStatsManager.playerLevelisHighEnough((Player) miner, LevelLists.shearsList, null, true))
				info.setReturnValue(false);
		}
	}

	@Inject(method = "useOn", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/context/UseOnContext;getItemInHand()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
	private void useOnBlockMixin(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
		ArrayList<Object> levelList = LevelLists.shearsList;
		if (!PlayerStatsManager.playerLevelisHighEnough(context.getPlayer(), levelList, null, true)) {
			context.getPlayer().displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			info.setReturnValue(InteractionResult.FAIL);
		}
	}
}
