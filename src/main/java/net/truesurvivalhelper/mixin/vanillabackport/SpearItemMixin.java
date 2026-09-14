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
