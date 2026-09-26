package net.levelz.item;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RareCandyItem extends Item {
	public RareCandyItem(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack stack = user.getItemInHand(hand);
		if (!world.isClientSide) {
			if (!user.isCreative())
				stack.shrink(1);
			PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) user).getPlayerStatsManager();
			if (ConfigInit.CONFIG.useIndependentExp)
				((PlayerSyncAccess) user)
						.addLevelExperience(playerStatsManager.getNextLevelExperience() - ((int) (playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience())));
			else
				((PlayerSyncAccess) user).levelUp(1, false, false);
		}
		return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
	}
}
