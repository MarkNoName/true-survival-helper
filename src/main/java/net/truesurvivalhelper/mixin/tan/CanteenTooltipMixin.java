package net.truesurvivalhelper.mixin.tan;

import java.util.List;
import java.util.Locale;

import net.levelz.stats.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.truesurvivalhelper.LevelZSkillAccess;
import net.truesurvivalhelper.MaterialLevels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.item.EmptyCanteenItem;

/**
 * Adds the red "Stamina &lt;level&gt;" tooltip line to canteens, in LevelZ's own style.
 * Mixes into ItemStack.getTooltipLines (like LevelZ's own tooltip Mixin does) rather
 * than the canteen item classes, because that's the entry point that actually has the
 * viewing player - Item.appendHoverText doesn't.
 *
 * Deliberately NOT going through LevelZ's customItemList for this: all 24 canteen
 * items (6 tiers x 4 fill states) share one tier -> level table, and registering every
 * one of them individually into customItemList would also make each show up as its
 * own separate line in LevelZ's per-skill "Skill Info" list - see
 * TshCustomItemRegistrar for the single synthetic per-tier entry used for that list
 * instead.
 */
@Mixin(ItemStack.class)
public class CanteenTooltipMixin {
	@Inject(method = "getTooltipLines", at = @At("RETURN"))
	private void tsh$appendCanteenTooltip(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir) {
		if (player == null) {
			return;
		}
		ItemStack stack = (ItemStack) (Object) this;
		Item item = stack.getItem();
		if (!(item instanceof EmptyCanteenItem canteen)) {
			return;
		}
		int required = MaterialLevels.forCanteenTier(((EmptyCanteenItemAccessor) canteen).tsh$getTier());
		if (!LevelZSkillAccess.meetsLevel(player, Skill.STAMINA, required)) {
			String key = "item.levelz." + Skill.STAMINA.toString().toLowerCase(Locale.ROOT) + ".tooltip";
			cir.getReturnValue().add(Component.translatable(key, required).withStyle(ChatFormatting.RED));
		}
	}
}
