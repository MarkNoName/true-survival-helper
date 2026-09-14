package net.truesurvivalhelper.mixin.levelz;

import java.util.Locale;

import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DiggerItem.class, priority = 900)
public class DiggerItemMixin {
	@Inject(method = "hurtEnemy", at = @At("HEAD"), cancellable = true)
	private void tsh$fixAxeCombatDurability(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> cir) {
		if (!ConfigInit.CONFIG.bindAxeDamageToSwordRestriction || !(stack.getItem() instanceof AxeItem)) {
			return;
		}
		if (!(attacker instanceof Player player)) {
			return;
		}
		String material = ((TieredItem) stack.getItem()).getTier().toString().toLowerCase(Locale.ROOT);
		if (!PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.swordList, material, true)) {
			return;
		}
		stack.hurtAndBreak(2, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		cir.setReturnValue(true);
	}
}
