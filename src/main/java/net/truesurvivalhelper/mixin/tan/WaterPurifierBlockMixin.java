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
 *
 * Injects right before the vanilla player.openMenu(...) call, matching LevelZ's own
 * furnace/anvil Mixins exactly (see AbstractFurnaceBlockMixin). This leaves the
 * block's own client-side branch (`if (isClientSide) return SUCCESS`) completely
 * untouched, so the arm swing comes for free from vanilla's normal
 * shouldSwing()-driven animation - exactly like the furnace - instead of this mod
 * trying to replicate it manually. A HEAD injection was tried first and had to
 * override the client branch's own SUCCESS return too, which is what silently broke
 * the swing (InteractionResult.FAIL never swings, see InteractionResult#shouldSwing).
 */
@Mixin(WaterPurifierBlock.class)
public class WaterPurifierBlockMixin {
	@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"), cancellable = true)
	private void tsh$requireFarmingLevel(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		if (!LevelZSkillAccess.meetsLevel(player, Skill.FARMING, 1)) {
			LevelZSkillAccess.sendDenialMessage(player, Skill.FARMING, 1);
			cir.setReturnValue(InteractionResult.FAIL);
		}
	}
}
