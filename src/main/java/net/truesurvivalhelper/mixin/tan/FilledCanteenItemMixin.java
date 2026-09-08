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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.item.FilledCanteenItem;

/**
 * Gates drinking a filled canteen behind the Stamina level for its tier. FilledCanteenItem
 * fully overrides use() rather than extending EmptyCanteenItem's, so it needs its own
 * injection alongside {@link EmptyCanteenItemMixin}.
 *
 * Extends EmptyCanteenItemMixin (not just its target class) purely so the @Shadow'd
 * {@code tier} field - declared on the vanilla-side in EmptyCanteenItem, not
 * FilledCanteenItem itself - resolves through the mixin class hierarchy the same way
 * FilledCanteenItem inherits it from EmptyCanteenItem. Mixin only searches a target's
 * own declared members for @Shadow, not its superclass, unless the mixin class
 * hierarchy mirrors it like this.
 */
@Mixin(FilledCanteenItem.class)
public abstract class FilledCanteenItemMixin extends EmptyCanteenItemMixin {
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void tsh$requireStaminaLevelFilled(Level level, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		int required = MaterialLevels.forCanteenTier(this.tier);
		if (!LevelZSkillAccess.meetsLevel(user, Skill.STAMINA, required)) {
			if (!level.isClientSide) {
				LevelZSkillAccess.sendDenialMessage(user, Skill.STAMINA, required);
			}
			cir.setReturnValue(InteractionResultHolder.fail(user.getItemInHand(hand)));
		}
	}
}
