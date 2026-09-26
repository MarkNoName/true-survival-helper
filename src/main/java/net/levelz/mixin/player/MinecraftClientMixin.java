package net.levelz.mixin.player;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;

@Environment(EnvType.CLIENT)
@Mixin(value = Minecraft.class, priority = 999)
public class MinecraftClientMixin {
	@Shadow
	@Nullable
	public LocalPlayer player;

	@Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
	private void handleBlockBreakingMixin(boolean breaking, CallbackInfo info) {
		if (restrictHandUsage(true)) {
			info.cancel();
		}
	}

	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void doAttackMixin(CallbackInfoReturnable<Boolean> info) {
		if (restrictHandUsage(false)) {
			info.setReturnValue(false);
		}
	}

	private boolean restrictHandUsage(boolean blockBreaking) {
		if (ConfigInit.CONFIG.lockedHandUsage && player != null && !player.isCreative()) {
			Item item = player.getMainHandItem().getItem();
			if (item != null && !item.equals(Items.AIR)) {
				ArrayList<Object> levelList = LevelLists.customItemList;
				if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(item).toString())) {
					String string = BuiltInRegistries.ITEM.getKey(item).toString();
					if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, string, true)) {
						player.displayClientMessage(
								Component.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).withStyle(ChatFormatting.RED),
								true);
						return true;
					}
				} else if (item instanceof TieredItem) {
					levelList = null;
					if (item instanceof SwordItem) {
						levelList = LevelLists.swordList;
					} else if (item instanceof AxeItem) {
						if (ConfigInit.CONFIG.bindAxeDamageToSwordRestriction && !blockBreaking) {
							levelList = LevelLists.swordList;
						} else {
							levelList = LevelLists.axeList;
						}
					} else if (item instanceof HoeItem) {
						levelList = LevelLists.hoeList;
					} else if (item instanceof PickaxeItem || item instanceof ShovelItem) {
						levelList = LevelLists.toolList;
					}
					if (levelList != null && ((TieredItem) item).getTier() != null) {
						String material = ((TieredItem) item).getTier().toString().toLowerCase();
						if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, true)) {
							player.displayClientMessage(Component
									.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
									.withStyle(ChatFormatting.RED), true);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
