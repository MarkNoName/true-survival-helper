package net.levelz.mixin.item;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.Skill;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;

@Mixin(PotionItem.class)
public class PotionItemMixin {
	@ModifyVariable(method = "finishUsingItem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/alchemy/PotionUtils;getMobEffects(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"), ordinal = 0)
	private List<MobEffectInstance> finishUsingMixin(List<MobEffectInstance> original, ItemStack stack, Level world, LivingEntity user) {
		if (user instanceof Player) {
			int alchemyLevel = ((PlayerStatsManagerAccess) (Player) user).getPlayerStatsManager().getSkillLevel(Skill.ALCHEMY);
			if (alchemyLevel >= ConfigInit.CONFIG.maxLevel && (float) alchemyLevel * ConfigInit.CONFIG.alchemyPotionChance > world.random.nextFloat()) {
				List<MobEffectInstance> newEffectList = new ArrayList<>();
				for (int i = 0; i < original.size(); i++) {
					newEffectList.add(
							new MobEffectInstance(original.get(i).getEffect(), original.get(i).getEffect().isInstantenous() ? original.get(i).getDuration() : original.get(i).getDuration() * 2,
									original.get(i).getEffect().isInstantenous() ? original.get(i).getAmplifier() + 1 : original.get(i).getAmplifier(), original.get(i).isAmbient(),
									original.get(i).isVisible(), original.get(i).showIcon()));
				}
				return newEffectList;
			}
		}
		return original;
	}
}
