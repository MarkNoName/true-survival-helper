package net.levelz.mixin.block;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BlastFurnaceBlock;

@Mixin(BlastFurnaceBlock.class)
public class BlastFurnaceBlockMixin {
	@Inject(method = "openContainer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"), cancellable = true)
	protected void openScreenMixin(Level world, BlockPos pos, Player player, CallbackInfo info) {
		ArrayList<Object> levelList = LevelLists.blastFurnaceList;
		if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
			player.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			info.cancel();
		}
	}
}
