package net.truesurvivalhelper.mixin.tan;

import net.levelz.stats.Skill;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.truesurvivalhelper.LevelZSkillAccess;
import net.truesurvivalhelper.MaterialLevels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.item.EmptyCanteenItem;

/**
 * Gates filling an empty canteen behind the Stamina level for its tier (leather/copper/
 * iron/gold/diamond/netherite), the same tier table used across the rest of the pack.
 * Canteens are plain Items with no shared vanilla material LevelZ recognises, so this
 * can't be expressed as LevelZ data - the tier check has to happen here.
 */
@Mixin(EmptyCanteenItem.class)
public abstract class EmptyCanteenItemMixin {
	@Shadow
	int tier;

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void tsh$requireStaminaLevel(Level level, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		int required = MaterialLevels.forCanteenTier(this.tier);
		if (!LevelZSkillAccess.meetsLevel(user, Skill.STAMINA, required)) {
			if (!level.isClientSide) {
				LevelZSkillAccess.sendDenialMessage(user, Skill.STAMINA, required);
			}
			cir.setReturnValue(InteractionResultHolder.fail(user.getItemInHand(hand)));
		}
	}
}
