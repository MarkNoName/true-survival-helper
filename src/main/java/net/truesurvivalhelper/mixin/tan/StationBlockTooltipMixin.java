package net.truesurvivalhelper.mixin.tan;

import java.util.List;
import java.util.Locale;

import net.levelz.stats.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.truesurvivalhelper.LevelZSkillAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.block.ThermoregulatorBlock;
import toughasnails.block.WaterPurifierBlock;

/**
 * Adds the red "Farming &lt;level&gt;" tooltip line to the water purifier and
 * thermoregulator item forms, in LevelZ's own style - same pattern as
 * {@link CanteenTooltipMixin}, and for the same reason: deliberately NOT going through
 * LevelZ's customBlockList (see TshCustomItemRegistrar for why registering these there
 * broke the actual block interaction).
 */
@Mixin(ItemStack.class)
public class StationBlockTooltipMixin {
	@Inject(method = "getTooltipLines", at = @At("RETURN"))
	private void tsh$appendStationBlockTooltip(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir) {
		if (player == null) {
			return;
		}
		ItemStack stack = (ItemStack) (Object) this;
		if (!(stack.getItem() instanceof BlockItem blockItem)) {
			return;
		}
		int required;
		if (blockItem.getBlock() instanceof WaterPurifierBlock) {
			required = 1;
		} else if (blockItem.getBlock() instanceof ThermoregulatorBlock) {
			required = 3;
		} else {
			return;
		}
		if (!LevelZSkillAccess.meetsLevelForTooltip(player, Skill.FARMING, required)) {
			String key = "item.levelz." + Skill.FARMING.toString().toLowerCase(Locale.ROOT) + ".tooltip";
			cir.getReturnValue().add(Component.translatable(key, required).withStyle(ChatFormatting.RED));
		}
	}
}
