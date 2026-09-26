package net.levelz.item;

import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class StrangePotionItem extends Item {
	public StrangePotionItem(Properties settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
		Player playerEntity = user instanceof Player ? (Player) user : null;
		if (playerEntity != null && !playerEntity.level().isClientSide()) {
			if (playerEntity instanceof ServerPlayer) {
				CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) playerEntity, stack);
			}

			for (Skill skill : Skill.listInRandomOrder(world.random)) {
				if (PlayerStatsManager.resetSkill(playerEntity, skill) && !ConfigInit.CONFIG.opStrangePotion)
					break;
			}

			if (!playerEntity.getAbilities().instabuild) {
				stack.shrink(1);
			}

			if (!playerEntity.getAbilities().instabuild) {
				if (stack.isEmpty()) {
					return new ItemStack(Items.GLASS_BOTTLE);
				}
				playerEntity.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
			}

			user.gameEvent(GameEvent.DRINK);
		}
		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, user, hand);
	}
}
