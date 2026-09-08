package net.truesurvivalhelper.mixin.biomemakeover;

import net.levelz.stats.Skill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.truesurvivalhelper.LevelZSkillAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import party.lemons.biomemakeover.item.EnchantedTotemItem;

/**
 * Gates the Enchanted Totem's death-save behind Alchemy level 12, independent of
 * LevelZ's own totemList (which is a single global requirement shared with the vanilla
 * Totem of Undying and can't give this item its own level). Failing the check simply
 * blocks activation - the totem is never consumed, matching "should only start working
 * at level 12".
 */
@Mixin(EnchantedTotemItem.class)
public class EnchantedTotemItemMixin {
	@Inject(method = "canActivate", at = @At("HEAD"), cancellable = true)
	private void tsh$requireAlchemyLevel(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof Player player && !LevelZSkillAccess.meetsLevel(player, Skill.ALCHEMY, 12)) {
			if (!entity.level().isClientSide()) {
				LevelZSkillAccess.sendDenialMessage(player, Skill.ALCHEMY, 12);
			}
			cir.setReturnValue(false);
		}
	}
}
