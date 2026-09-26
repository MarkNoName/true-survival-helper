package net.levelz.mixin.block;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.TagInit;
import net.levelz.stats.Skill;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BushBlock.class)
public abstract class PlantBlockMixin extends Block {
	public PlantBlockMixin(Properties settings) {
		super(settings);
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		super.playerWillDestroy(world, pos, state, player);
		if (!world.isClientSide && player != null && !player.isCreative()) {
			int farmingLevel = ((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(Skill.FARMING);
			if (farmingLevel >= ConfigInit.CONFIG.farmingBase && (float) farmingLevel * ConfigInit.CONFIG.farmingChanceBonus > world.random.nextFloat()) {
				List<ItemStack> list = Block.getDrops(state, (ServerLevel) world, pos, null);
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).is(TagInit.FARM_ITEMS)) {
						Block.popResource(world, pos, list.get(i));
						break;
					}
				}
			}
		}
	}
}
