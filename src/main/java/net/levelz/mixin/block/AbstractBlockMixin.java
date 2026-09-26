package net.levelz.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerBreakBlockAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockBehaviour.class)
public class AbstractBlockMixin {
	@ModifyVariable(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"), ordinal = 0)
	private int calcBlockBreakingDeltaMixin(int original, BlockState state, Player player, BlockGetter world, BlockPos pos) {
		return (int) (original * ((PlayerBreakBlockAccess) player.getInventory()).getBreakingAbstractBlockDelta());
	}
}
