package net.levelz.init;

import java.util.ArrayList;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.data.LevelLists;
import net.levelz.mixin.entity.EntityAccessor;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

public class EventInit {
	public static void init() {
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			((PlayerSyncAccess) player).syncStats(false);
		});
		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (!player.isCreative() && !player.isSpectator()) {
				ArrayList<Object> customList = LevelLists.customItemList;
				String string = BuiltInRegistries.ITEM.getKey(player.getItemInHand(hand).getItem()).toString();
				if (!customList.isEmpty() && !PlayerStatsManager.playerLevelisHighEnough(player, customList, string, true)) {
					player.displayClientMessage(
							Component.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2)).withStyle(ChatFormatting.RED),
							true);
					return InteractionResultHolder.fail(player.getItemInHand(hand));
				}
			}
			return InteractionResultHolder.pass(ItemStack.EMPTY);
		});

		UseBlockCallback.EVENT.register((player, world, hand, result) -> {
			if (!player.isCreative() && !player.isSpectator()) {
				BlockPos blockPos = result.getBlockPos();
				if (world.mayInteract(player, blockPos)) {
					String string = BuiltInRegistries.BLOCK.getKey(world.getBlockState(blockPos).getBlock()).toString();
					ArrayList<Object> customList = LevelLists.customBlockList;
					if (!customList.isEmpty() && customList.contains(string)) {
						if (!PlayerStatsManager.playerLevelisHighEnough(player, customList, string, true)) {
							player.displayClientMessage(Component.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2))
									.withStyle(ChatFormatting.RED), true);
							return InteractionResult.sidedSuccess(false);
						}
					}
				}
			}
			return InteractionResult.PASS;
		});

		UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
			if (!player.isCreative() && !player.isSpectator()) {
				if (!entity.hasControllingPassenger() || !((EntityAccessor) entity).callCanAddPassenger(player)) {
					String string = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
					ArrayList<Object> customList = LevelLists.customEntityList;
					if (!customList.isEmpty() && customList.contains(string)) {
						if (!PlayerStatsManager.playerLevelisHighEnough(player, customList, string, true)) {
							player.displayClientMessage(Component.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2))
									.withStyle(ChatFormatting.RED), true);
							return InteractionResult.sidedSuccess(false);
						}
					}
				}
			}
			return InteractionResult.PASS;
		});
	}
}
