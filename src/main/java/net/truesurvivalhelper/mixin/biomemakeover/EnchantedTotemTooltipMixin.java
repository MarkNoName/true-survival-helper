package net.truesurvivalhelper.mixin.biomemakeover;

import java.util.List;
import java.util.Locale;

import net.levelz.stats.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.truesurvivalhelper.LevelZSkillAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import party.lemons.biomemakeover.item.EnchantedTotemItem;

/**
 * Adds the red "Alchemy &lt;level&gt;" tooltip line to the enchanted totem, in LevelZ's
 * own style - same pattern as {@code net.truesurvivalhelper.mixin.tan.CanteenTooltipMixin}.
 * Deliberately NOT going through LevelZ's customItemList (see TshCustomItemRegistrar
 * for why registering it there broke the "stay silent on right-click" requirement) - a
 * passive tooltip on hover doesn't fire during an actual death-save attempt, so it
 * doesn't run into the same problem.
 */
@Mixin(ItemStack.class)
public class EnchantedTotemTooltipMixin {
	@Inject(method = "getTooltipLines", at = @At("RETURN"))
	private void tsh$appendTotemTooltip(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir) {
		if (player == null) {
			return;
		}
		ItemStack stack = (ItemStack) (Object) this;
		if (!(stack.getItem() instanceof EnchantedTotemItem)) {
			return;
		}
		if (!LevelZSkillAccess.meetsLevelForTooltip(player, Skill.ALCHEMY, 12)) {
			String key = "item.levelz." + Skill.ALCHEMY.toString().toLowerCase(Locale.ROOT) + ".tooltip";
			cir.getReturnValue().add(Component.translatable(key, 12).withStyle(ChatFormatting.RED));
		}
	}
}
