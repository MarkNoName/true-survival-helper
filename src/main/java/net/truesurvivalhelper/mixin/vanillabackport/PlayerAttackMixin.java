package net.truesurvivalhelper.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.level.items.spear.SpearItem;
import net.levelz.stats.Skill;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.truesurvivalhelper.LevelZSkillAccess;
import net.truesurvivalhelper.MaterialLevels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Blocks left-click melee attacks with a spear below the required Agility level.
 * VanillaBackport deals the actual attack damage through the normal vanilla
 * Player#attack pipeline (its own spear Mixin only hooks the tail, for durability),
 * so this has to cancel the whole attack at the head - by the time SpearItem#hurtEnemy
 * would run, the hit has already landed. See {@link SpearItemMixin} for why that
 * method didn't work.
 */
@Mixin(Player.class)
public abstract class PlayerAttackMixin {
	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	private void tsh$blockSpearAttack(Entity target, CallbackInfo ci) {
		Player self = (Player) (Object) this;
		ItemStack stack = self.getMainHandItem();
		if (!(stack.getItem() instanceof SpearItem spear)) {
			return;
		}
		int required = MaterialLevels.forMaterial(spear.getTier().toString());
		if (!LevelZSkillAccess.meetsLevel(self, Skill.AGILITY, required)) {
			if (!self.level().isClientSide()) {
				LevelZSkillAccess.sendDenialMessage(self, Skill.AGILITY, required);
			}
			ci.cancel();
		}
	}
}
