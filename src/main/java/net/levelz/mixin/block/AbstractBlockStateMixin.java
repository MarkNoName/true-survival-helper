package net.levelz.mixin.block;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerBreakBlockAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class AbstractBlockStateMixin {
	@Inject(method = "attack", at = @At(value = "HEAD"))
	private void onBlockBreakStartMixin(Level world, BlockPos pos, Player player, CallbackInfo info) {
		Item item = player.getItemInHand(player.getUsedItemHand()).getItem();
		ArrayList<Object> levelList = LevelLists.customItemList;

		if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(item).toString())) {
			if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, BuiltInRegistries.ITEM.getKey(item).toString(), true))
				((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(false);
		} else if (item instanceof DiggerItem) {
			if (item instanceof HoeItem) {
				levelList = LevelLists.hoeList;
			} else if (item instanceof AxeItem) {
				levelList = LevelLists.axeList;
			} else {
				levelList = LevelLists.toolList;
			}
			if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, ((DiggerItem) item).getTier().toString().toLowerCase(), true)) {
				((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(false);
			} else
				((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(true);
		} else
			((PlayerBreakBlockAccess) player.getInventory()).setInventoryBlockBreakable(true);

		PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
		int playerMiningLevel = playerStatsManager.getSkillLevel(Skill.MINING);
		if (playerMiningLevel < ConfigInit.CONFIG.maxLevel) {
			if (PlayerStatsManager.listContainsItemOrBlock(player, 1, BuiltInRegistries.BLOCK.getId(world.getBlockState(pos).getBlock()))) {
				((PlayerBreakBlockAccess) player.getInventory()).setAbstractBlockBreakDelta(ConfigInit.CONFIG.miningLockedMultiplicator);
			} else if (playerMiningLevel < 5) {
				((PlayerBreakBlockAccess) player.getInventory()).setAbstractBlockBreakDelta(1.2F - playerMiningLevel * 0.0475F);
			} else
				((PlayerBreakBlockAccess) player.getInventory()).setAbstractBlockBreakDelta(1.0F);
		} else
			((PlayerBreakBlockAccess) player.getInventory()).setAbstractBlockBreakDelta(1.0F);
	}
}
