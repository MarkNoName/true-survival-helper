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
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(BucketItem.class)
public class BucketItemMixin {
	@Inject(method = "use", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void useMixin(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info, ItemStack itemStack, BlockHitResult blockHitResult) {
		if (world.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof BucketPickup) {
			ArrayList<Object> levelList = LevelLists.bucketList;
			if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, null, true)) {
				user.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
				info.setReturnValue(InteractionResultHolder.fail(itemStack));
			}
		}
	}

	@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BucketItem;emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void useBucketMixin(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info, ItemStack itemStack, BlockHitResult blockHitResult) {
		ArrayList<Object> levelList = LevelLists.bucketList;
		if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, null, true)) {
			user.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			info.setReturnValue(InteractionResultHolder.fail(itemStack));
		}
	}
}
