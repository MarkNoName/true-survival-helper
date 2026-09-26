package net.levelz.mixin.item;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
	@Inject(method = "dispenseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private static void dispenseArmorMixin(BlockSource pointer, ItemStack armor, CallbackInfoReturnable<Boolean> info, BlockPos blockPos, List<LivingEntity> list, LivingEntity livingEntity) {
		if (livingEntity instanceof Player && armor.getItem() instanceof ArmorItem) {
			ArrayList<Object> levelList = LevelLists.customItemList;
			try {
				if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(armor.getItem()).toString())) {
					if (!PlayerStatsManager.playerLevelisHighEnough((Player) livingEntity, levelList, BuiltInRegistries.ITEM.getKey(armor.getItem()).toString(), true))
						info.setReturnValue(false);
				} else {
					levelList = LevelLists.armorList;
					if (!PlayerStatsManager.playerLevelisHighEnough((Player) livingEntity, levelList, ((ArmorItem) armor.getItem()).getMaterial().getName().toLowerCase(), true))
						info.setReturnValue(false);
				}
			} catch (AbstractMethodError ignore) {
			}
		}
	}
}
