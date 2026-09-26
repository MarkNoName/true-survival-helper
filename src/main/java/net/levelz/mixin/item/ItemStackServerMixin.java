package net.levelz.mixin.item;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.Skill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackServerMixin {
	@ModifyVariable(method = "hurt(ILnet/minecraft/util/RandomSource;Lnet/minecraft/server/level/ServerPlayer;)Z", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"), ordinal = 1)
	private int damageMixin(int original, int amount, RandomSource random, @Nullable ServerPlayer player) {
		if (player != null) {
			if ((float) ((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(Skill.SMITHING) * ConfigInit.CONFIG.smithingToolChance > random.nextFloat()) {
				return original + 1;
			}
		}
		return original;
	}
}
