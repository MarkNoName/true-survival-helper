package net.truesurvivalhelper.mixin.tan;

import net.levelz.stats.Skill;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.truesurvivalhelper.LevelZSkillAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.block.WaterPurifierBlock;

/**
 * Gates opening the Water Purifier's screen behind Farming level 1. This is a block
 * right-click interaction, not covered by any LevelZ station-block category.
 */
@Mixin(WaterPurifierBlock.class)
public class WaterPurifierBlockMixin {
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void tsh$requireFarmingLevel(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		if (!LevelZSkillAccess.meetsLevel(player, Skill.FARMING, 1)) {
			if (!level.isClientSide) {
				LevelZSkillAccess.sendDenialMessage(player, Skill.FARMING, 1);
			}
			cir.setReturnValue(InteractionResult.FAIL);
		}
	}
}
