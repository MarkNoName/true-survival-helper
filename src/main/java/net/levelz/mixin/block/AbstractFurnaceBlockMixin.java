package net.levelz.mixin.block;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(AbstractFurnaceBlock.class)
public abstract class AbstractFurnaceBlockMixin extends BaseEntityBlock {
	public AbstractFurnaceBlockMixin(Properties settings) {
		super(settings);
	}

	@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/AbstractFurnaceBlock;openContainer(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
	private void onUseMixin(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> info) {
		if (!((Object) this instanceof BlastFurnaceBlock) && !((Object) this instanceof SmokerBlock)) {
			String string = BuiltInRegistries.BLOCK.getKey(world.getBlockState(pos).getBlock()).toString();
			ArrayList<Object> customList = LevelLists.customBlockList;
			if (!customList.isEmpty() && customList.contains(string)) {
				if (!PlayerStatsManager.playerLevelisHighEnough(player, customList, string, true)) {
					player.displayClientMessage(
							Component.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) + ".tooltip", customList.get(customList.indexOf(string) + 2)).withStyle(ChatFormatting.RED),
							true);
					info.setReturnValue(InteractionResult.FAIL);
				}
			} else {
				ArrayList<Object> levelList = LevelLists.furnaceList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
					player.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
					info.setReturnValue(InteractionResult.FAIL);
				}
			}
		}
	}
}
