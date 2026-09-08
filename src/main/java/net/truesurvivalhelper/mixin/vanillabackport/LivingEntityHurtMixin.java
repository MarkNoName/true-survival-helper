package net.truesurvivalhelper.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.level.items.spear.SpearItem;
import net.levelz.stats.Skill;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.truesurvivalhelper.LevelZSkillAccess;
import net.truesurvivalhelper.MaterialLevels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The real fix for blocking left-click spear damage: VanillaBackport's spears use a
 * custom kinetic/piercing hit system (they can hit multiple entities in one swing),
 * which does NOT go through the normal vanilla Player#attack(Entity) call that
 * {@link PlayerAttackMixin} hooks - that mixin never fires for a spear hit, which is
 * why cancelling it didn't stop anything. Every damage source still has to flow
 * through LivingEntity#hurt to actually reduce health, regardless of which of
 * VanillaBackport's own hit-detection paths produced it, so gating here catches all
 * of them reliably.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityHurtMixin {
	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	private void tsh$blockSpearDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		Entity attackerEntity = source.getEntity();
		if (!(attackerEntity instanceof Player attacker)) {
			return;
		}
		if (!(attacker.getMainHandItem().getItem() instanceof SpearItem spear)) {
			return;
		}
		int required = MaterialLevels.forMaterial(spear.getTier().toString());
		if (!LevelZSkillAccess.meetsLevel(attacker, Skill.AGILITY, required)) {
			if (!attacker.level().isClientSide()) {
				LevelZSkillAccess.sendDenialMessage(attacker, Skill.AGILITY, required);
			}
			cir.setReturnValue(false);
		}
	}
}
