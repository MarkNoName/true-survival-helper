package net.levelz.mixin.misc;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	@ModifyVariable(method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;"), ordinal = 0)
	private static int getEquipmentLevelMixin(int original, Enchantment enchantment, LivingEntity entity) {
		if (original != 0 && entity instanceof Player player && (float) ((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(Skill.ALCHEMY)
				* ConfigInit.CONFIG.alchemyEnchantmentChance > entity.level().getRandom().nextFloat())
			return original += 1;
		else
			return original;
	}

	@Inject(method = "doPostDamageEffects", at = @At("HEAD"), cancellable = true)
	private static void onTargetDamagedMixin(LivingEntity user, Entity target, CallbackInfo info) {
		if (user instanceof Player) {
			Item item = user.getItemInHand(user.getUsedItemHand()).getItem();
			if (item instanceof TieredItem) {
				Player playerEntity = (Player) user;
				ArrayList<Object> levelList = LevelLists.customItemList;
				if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(item).toString())) {
					if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, BuiltInRegistries.ITEM.getKey(item).toString(), true))
						info.cancel();
				} else {
					levelList = null;
					if (item instanceof SwordItem) {
						levelList = LevelLists.swordList;
					} else if (item instanceof AxeItem)
						levelList = LevelLists.axeList;
					else if (item instanceof HoeItem)
						levelList = LevelLists.hoeList;
					else if (item instanceof PickaxeItem || item instanceof ShovelItem)
						levelList = LevelLists.toolList;
					if (levelList != null)
						if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((TieredItem) item).getTier().toString().toLowerCase(), true))
							info.cancel();
				}
			}
		}
	}

	@Inject(method = "doPostHurtEffects", at = @At("HEAD"), cancellable = true)
	private static void onUserDamagedMixin(LivingEntity user, Entity attacker, CallbackInfo info) {
		if (user instanceof Player) {
			Item item = user.getItemInHand(user.getUsedItemHand()).getItem();
			if (item instanceof TieredItem) {
				Player playerEntity = (Player) user;
				ArrayList<Object> levelList = LevelLists.customItemList;
				if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(item).toString())) {
					if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, BuiltInRegistries.ITEM.getKey(item).toString(), true))
						info.cancel();
				} else {
					levelList = null;
					if (item instanceof SwordItem) {
						levelList = LevelLists.swordList;
					} else if (item instanceof AxeItem)
						levelList = LevelLists.axeList;
					else if (item instanceof HoeItem)
						levelList = LevelLists.hoeList;
					else if (item instanceof PickaxeItem || item instanceof ShovelItem)
						levelList = LevelLists.toolList;
					if (levelList != null)
						if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((TieredItem) item).getTier().toString().toLowerCase(), true))
							info.cancel();
				}
			}
		}
	}
}
