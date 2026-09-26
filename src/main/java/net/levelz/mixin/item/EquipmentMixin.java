package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Equipable.class)
public interface EquipmentMixin {
	@Inject(method = "swapWithEquipmentSlot", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	default public void equipAndSwapMixin(Item item, Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info, ItemStack itemStack,
			EquipmentSlot equipmentSlot, ItemStack itemStack2) {
		if (itemStack.getItem() instanceof ElytraItem) {
			ArrayList<Object> levelList = LevelLists.elytraList;
			if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, null, true)) {
				user.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
				info.setReturnValue(InteractionResultHolder.fail(itemStack));
			}
		} else if (itemStack.getItem() instanceof ArmorItem) {
			ArrayList<Object> levelList = LevelLists.customItemList;
			if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString())) {
				String string = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
				if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, string, true)) {
					user.displayClientMessage(
							Component.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).withStyle(ChatFormatting.RED),
							true);
					info.setReturnValue(InteractionResultHolder.fail(itemStack));
				}
			} else {
				String string = ((ArmorItem) itemStack.getItem()).getMaterial().getName().toLowerCase();
				levelList = LevelLists.armorList;
				if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, string, true)) {
					user.displayClientMessage(
							Component.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).withStyle(ChatFormatting.RED),
							true);
					info.setReturnValue(InteractionResultHolder.fail(itemStack));
				}
			}
		}
	}
}
