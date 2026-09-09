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

/**
 * Fixes an inconsistency in LevelZ's own axe restrictions: with
 * bindAxeDamageToSwordRestriction on (this pack's setting), axe combat damage is gated
 * by Strength/swordList (PlayerEntityMixin#getUnlockedDamage), but LevelZ's own
 * MiningToolItemMixin#postHitMixin - which cancels DiggerItem#hurtEnemy, the method that
 * actually calls ItemStack#hurtAndBreak - always checks Farming/axeList for axes instead,
 * regardless of that config flag. Net effect: a player who meets the Strength
 * requirement but not the Farming one can already deal full damage, but the axe never
 * loses durability, since postHitMixin still blocks hurtAndBreak on the Farming check.
 *
 * This only steps in for that exact combination (axe, config flag on, Strength
 * requirement met) and forces durability loss through - replicating vanilla
 * DiggerItem#hurtEnemy's own hurtAndBreak(2, ...) call, then cancelling so
 * MiningToolItemMixin's Farming-based check never runs at all. A lower @Mixin priority
 * than the default (1000) makes sure this HEAD injection's cancellation is evaluated
 * before LevelZ's own - Mixin checks for cancellation between each injected handler at
 * the same point, in priority order, so an earlier cancellation skips later handlers
 * entirely. Every other case (non-axes, the config flag off, or Strength genuinely not
 * met) falls through untouched to LevelZ's own logic.
 */
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
