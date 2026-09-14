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
		if (!LevelZSkillAccess.meetsLevelForTooltip(player, Skill.STAMINA, required)) {
			String key = "item.levelz." + Skill.STAMINA.toString().toLowerCase(Locale.ROOT) + ".tooltip";
			cir.getReturnValue().add(Component.translatable(key, required).withStyle(ChatFormatting.RED));
		}
	}
}
