package net.levelz.mixin.item;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(BowItem.class)
public class BowItemMixin {
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
			levelList = LevelLists.bowList;
			if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, null, true)) {
				user.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
				info.setReturnValue(InteractionResultHolder.fail(itemStack));
			}
		}
	}

	@Inject(method = "releaseUsing", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void onStoppedUsingMixin(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfo info, Player playerEntity, boolean bl, ItemStack itemStack, int i,
			float f, boolean bl2, ArrowItem arrowItem, AbstractArrow persistentProjectileEntity, int j) {
		int archeryLevel = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager().getSkillLevel(Skill.ARCHERY);
		persistentProjectileEntity.setBaseDamage(persistentProjectileEntity.getBaseDamage()
				+ (archeryLevel >= ConfigInit.CONFIG.maxLevel && ConfigInit.CONFIG.archeryDoubleDamageChance > world.random.nextFloat() ? persistentProjectileEntity.getBaseDamage() * 2D
						: (double) archeryLevel * ConfigInit.CONFIG.archeryBowExtraDamage));
	}
}
