package net.levelz.mixin.entity;

import java.util.ArrayList;
import java.util.Iterator;

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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Villager.class)
public abstract class VillagerEntityMixin extends AbstractVillager {
	@Shadow
	@Nullable
	private Player lastTradedPlayer;

	public VillagerEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;startTrading(Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
	private void interactMobMixin(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		ArrayList<Object> levelList = LevelLists.villagerList;
		if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
			this.setUnhappy();
			player.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			info.setReturnValue(InteractionResult.FAIL);
		}
	}

	@Inject(method = "rewardTradeXp", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
	protected void afterUsingMixin(MerchantOffer offer, CallbackInfo info, int i) {
		if (ConfigInit.CONFIG.tradingXPMultiplier > 0.0F)
			LevelExperienceOrbEntity.spawn((ServerLevel) this.level(), this.position().add(0.0D, 0.5D, 0.0D),
					(int) (i * ConfigInit.CONFIG.tradingXPMultiplier
							* (lastTradedPlayer != null ? 1.0F + ((PlayerStatsManagerAccess) lastTradedPlayer).getPlayerStatsManager().getSkillLevel(Skill.TRADE) * ConfigInit.CONFIG.tradeXPBonus : 1.0F)
							* (ConfigInit.CONFIG.dropXPbasedOnLvl && lastTradedPlayer != null
									? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) lastTradedPlayer).getPlayerStatsManager().getOverallLevel()
									: 1.0F)));
	}

	@Inject(method = "updateSpecialPrices", at = @At(value = "TAIL"))
	private void prepareOffersForMixin(Player player, CallbackInfo info) {
		if (!player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
			Iterator<MerchantOffer> var5 = this.getOffers().iterator();
			while (var5.hasNext()) {
				MerchantOffer tradeOffer2 = var5.next();
				tradeOffer2.addToSpecialPriceDiff(-(int) (((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(Skill.TRADE) * ConfigInit.CONFIG.tradeBonus / 100.0D
						* tradeOffer2.getBaseCostA().getCount()));
			}
		}
	}

	@Inject(method = "setLastHurtByMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;onReputationEvent(Lnet/minecraft/world/entity/ai/village/ReputationEventType;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/ReputationEventHandler;)V"), cancellable = true)
	private void setAttackerMixin(@Nullable LivingEntity attacker, CallbackInfo info) {
		if (attacker != null && attacker instanceof Player && ConfigInit.CONFIG.tradeReputation
				&& ((PlayerStatsManagerAccess) (Player) attacker).getPlayerStatsManager().getSkillLevel(Skill.TRADE) >= ConfigInit.CONFIG.maxLevel) {
			super.setLastHurtByMob(attacker);
			info.cancel();
		}
	}

	@Shadow
	private void setUnhappy() {
	}
}
