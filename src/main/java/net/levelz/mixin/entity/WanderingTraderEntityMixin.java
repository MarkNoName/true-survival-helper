package net.levelz.mixin.entity;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderEntityMixin extends AbstractVillager {
	public WanderingTraderEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/WanderingTrader;setTradingPlayer(Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
	private void interactMobMixin(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		ArrayList<Object> levelList = LevelLists.wanderingTraderList;
		if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
			player.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			info.setReturnValue(InteractionResult.FAIL);
		}
	}

	@Inject(method = "rewardTradeXp", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
	protected void afterUsingMixin(MerchantOffer offer, CallbackInfo info, int i) {
		if (ConfigInit.CONFIG.tradingXPMultiplier > 0.0F)
			LevelExperienceOrbEntity.spawn((ServerLevel) this.level(), this.position().add(0.0D, 0.5D, 0.0D), (int) (i * ConfigInit.CONFIG.tradingXPMultiplier
					* (this.getTradingPlayer() != null ? 1.0F + ((PlayerStatsManagerAccess) this.getTradingPlayer()).getPlayerStatsManager().getSkillLevel(Skill.TRADE) * ConfigInit.CONFIG.tradeXPBonus : 1.0F)
					* (ConfigInit.CONFIG.dropXPbasedOnLvl && this.getTradingPlayer() != null
							? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) this.getTradingPlayer()).getPlayerStatsManager().getOverallLevel()
							: 1.0F)));
	}
}
