package net.truesurvivalhelper.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.level.items.spear.SpearItem;
import net.levelz.stats.Skill;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.truesurvivalhelper.LevelZSkillAccess;
import net.truesurvivalhelper.MaterialLevels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Gates the spear's right-click throw/lunge behind an Agility level keyed off its
 * material tier. The left-click melee block lives separately in
 * {@link PlayerAttackMixin}, targeting Player#attack directly - SpearItem#hurtEnemy
 * turned out to only apply the durability hit, not the actual damage (VanillaBackport
 * deals damage through the normal vanilla attack pipeline and only calls hurtEnemy
 * afterward, at the tail of its own Player#attack Mixin, as a side effect), so
 * cancelling it never actually blocked anything.
 *
 * getTier() is a normal public vanilla method inherited from TieredItem (not declared
 * on SpearItem itself), so it's read via a plain cast rather than @Shadow - @Shadow
 * only resolves members declared directly on the mixin's own target class, and
 * SpearItem doesn't redeclare getTier().
 */
@Mixin(SpearItem.class)
public abstract class SpearItemMixin {
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void tsh$blockRightClick(Level level, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		TieredItem self = (TieredItem) (Object) this;
		int required = MaterialLevels.forMaterial(self.getTier().toString());
		if (!LevelZSkillAccess.meetsLevel(user, Skill.AGILITY, required)) {
			if (!level.isClientSide) {
				LevelZSkillAccess.sendDenialMessage(user, Skill.AGILITY, required);
			}
			cir.setReturnValue(InteractionResultHolder.fail(user.getItemInHand(hand)));
		}
	}
}
