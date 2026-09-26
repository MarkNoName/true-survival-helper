package net.levelz.mixin.entity;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.access.MobEntityAccess;
import net.levelz.access.PlayerDropAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	protected int lastHurtByPlayerTime;

	@Shadow
	@Nullable
	protected Player lastHurtByPlayer;

	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	private void hurtMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		if (source.getEntity() instanceof Player player && !PlayerStatsManager.weaponLevelisHighEnough(player)) {
			info.setReturnValue(false);
		}
	}

	@ModifyVariable(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageProtection(Ljava/lang/Iterable;Lnet/minecraft/world/damagesource/DamageSource;)I", shift = At.Shift.AFTER), ordinal = 0)
	private int modifyAppliedDamageMixin(int original, DamageSource source, float amount) {
		if (source == this.damageSources().fall() && (Object) this instanceof Player player) {
			return (int) (original + ((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(Skill.AGILITY) * ConfigInit.CONFIG.movementFallBonus);
		} else {
			return original;
		}
	}

	@ModifyVariable(method = "checkTotemDeathProtection", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
	private ItemStack tryUseTotemMixin(ItemStack original) {
		if ((Object) this instanceof Player player) {
			ArrayList<Object> levelList = LevelLists.totemList;
			if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
				return ItemStack.EMPTY;
			}
		}
		return original;
	}

	@Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/InteractionHand;values()[Lnet/minecraft/world/InteractionHand;"), cancellable = true)
	private void tryUseTotemMixin(DamageSource source, CallbackInfoReturnable<Boolean> info) {
		if ((Object) this instanceof Player player) {
			if (((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(Skill.LUCK) >= ConfigInit.CONFIG.maxLevel
					&& player.level().getRandom().nextFloat() < ConfigInit.CONFIG.luckSurviveChance) {
				player.setHealth(1.0F);
				player.removeAllEffects();
				player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
				player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0));
				info.setReturnValue(true);
			}
		}
	}

	@Inject(method = "dropAllDeathLoot", at = @At(value = "HEAD"), cancellable = true)
	protected void dropMixin(DamageSource source, CallbackInfo info) {
		if (!((Object) this instanceof Player) && lastHurtByPlayer != null && this.lastHurtByPlayerTime > 0 && ConfigInit.CONFIG.disableMobFarms
				&& !((PlayerDropAccess) lastHurtByPlayer).allowMobDrop()) {
			info.cancel();
		}
	}

	@Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropAllDeathLoot(Lnet/minecraft/world/damagesource/DamageSource;)V"))
	private void onDeathMixin(DamageSource source, CallbackInfo info) {
		if (lastHurtByPlayer != null && this.lastHurtByPlayerTime > 0 && ConfigInit.CONFIG.disableMobFarms) {
			((PlayerDropAccess) lastHurtByPlayer).increaseKilledMobStat(this.level().getChunk(this.blockPosition()));
		}
	}

	@Inject(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
	protected void dropXpMixin(CallbackInfo info) {
		if (ConfigInit.CONFIG.mobXPMultiplier > 0.0F) {
			if (!ConfigInit.CONFIG.spawnerMobXP && (Object) this instanceof Mob mobEntity && ((MobEntityAccess) mobEntity).isSpawnerMob()) {
			} else {
				LevelExperienceOrbEntity.spawn((ServerLevel) this.level(), this.position(),
						(int) (this.getExperienceReward() * ConfigInit.CONFIG.mobXPMultiplier
								* (ConfigInit.CONFIG.dropXPbasedOnLvl && this.lastHurtByPlayer != null
										? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) this.lastHurtByPlayer).getPlayerStatsManager().getOverallLevel()
										: 1.0F)));
			}
		}
	}

	@Shadow
	protected int getExperienceReward() {
		return 0;
	}
}
