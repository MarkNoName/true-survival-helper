package net.levelz.mixin.player;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerDropAccess;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerStatsManagerAccess, PlayerDropAccess {
	private final Player playerEntity = (Player) (Object) this;
	private final PlayerStatsManager playerStatsManager = new PlayerStatsManager(playerEntity);
	private boolean isCrit;
	private int killedMobsInChunk;
	@Nullable
	private ChunkAccess killedMobChunk;

	public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	public void readCustomDataFromNbtMixin(CompoundTag tag, CallbackInfo info) {
		this.playerStatsManager.readNbt(tag);
		playerEntity.getAttribute(Attributes.MOVEMENT_SPEED)
				.setBaseValue(ConfigInit.CONFIG.movementBase + (double) playerStatsManager.getSkillLevel(Skill.AGILITY) * ConfigInit.CONFIG.movementBonus);
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	public void writeCustomDataToNbtMixin(CompoundTag tag, CallbackInfo info) {
		this.playerStatsManager.writeNbt(tag);
	}

	@Redirect(method = "causeFoodExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"), require = 0)
	private void addExhaustion(FoodData hungerManager, float exhaustion) {
		exhaustion *= ConfigInit.CONFIG.staminaBase - ((float) playerStatsManager.getSkillLevel(Skill.STAMINA) * ConfigInit.CONFIG.staminaBonus);
		hungerManager.addExhaustion(exhaustion);
	}

	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	private void attackMixin(Entity target, CallbackInfo info) {
		if (!PlayerStatsManager.weaponLevelisHighEnough(playerEntity)) {
			info.cancel();
		}
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getKnockbackBonus(Lnet/minecraft/world/entity/LivingEntity;)I"), ordinal = 0, require = 0)
	private boolean attacGetKnockbackkMixin(boolean original) {
		if (playerEntity.level().getRandom().nextFloat() < (float) playerStatsManager.getSkillLevel(Skill.LUCK) * ConfigInit.CONFIG.luckCritBonus) {
			isCrit = true;
			return true;
		} else
			isCrit = false;
		return original;
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z", ordinal = 0), ordinal = 1, require = 0)
	private boolean attackIsSprintingMixin(boolean original) {
		if (isCrit) {
			return true;
		} else
			return original;
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getFireAspect(Lnet/minecraft/world/entity/LivingEntity;)I"), ordinal = 2, require = 0)
	private boolean attackGetFireAspectMixin(boolean original) {
		if (isCrit) {
			return true;
		} else
			return original;
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", shift = At.Shift.AFTER), ordinal = 0, require = 0)
	private float attackGetItemMixin(float original) {
		if (playerStatsManager.getSkillLevel(Skill.STRENGTH) >= ConfigInit.CONFIG.maxLevel && ConfigInit.CONFIG.attackDoubleDamageChance > playerEntity.level().getRandom().nextFloat()) {
			return original * 2F;
		} else
			return isCrit ? original * ConfigInit.CONFIG.attackCritDmgBonus : original;
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;getAttackStrengthScale(F)F"), ordinal = 0, require = 0)
	private float attackGetAttackCooldownProgressMixin(float original) {
		return getUnlockedDamage(original, false);
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;getAttackStrengthScale(F)F"), ordinal = 1, require = 0)
	private float attackGetAttackCooldownProgressMixinTwo(float original) {
		return getUnlockedDamage(original, true);
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getFireAspect(Lnet/minecraft/world/entity/LivingEntity;)I"), ordinal = 1, require = 0)
	private int attackGetFireAspectMixin(int original) {
		return (int) getUnlockedDamage((float) original, true);
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getKnockbackBonus(Lnet/minecraft/world/entity/LivingEntity;)I"), ordinal = 0, require = 0)
	private int attackGetKnockbackMixin(int original) {
		return (int) getUnlockedDamage((float) original, true);
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"), ordinal = 3)
	private float attackGetSweepingMultiplierMixin(float original) {
		return getUnlockedDamage(original, true);
	}

	private float getUnlockedDamage(float original, boolean zero) {
		Item item = playerEntity.getMainHandItem().getItem();
		if (!item.equals(Items.AIR)) {
			ArrayList<Object> levelList = LevelLists.customItemList;
			if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(item).toString())) {
				if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, BuiltInRegistries.ITEM.getKey(item).toString(), true))
					return zero ? 0 : 1.0F;
			} else if (item instanceof TieredItem) {
				levelList = null;
				if (item instanceof SwordItem) {
					levelList = LevelLists.swordList;
				} else if (item instanceof AxeItem) {
					if (ConfigInit.CONFIG.bindAxeDamageToSwordRestriction)
						levelList = LevelLists.swordList;
					else
						levelList = LevelLists.axeList;
				} else if (item instanceof HoeItem)
					levelList = LevelLists.hoeList;
				else if (item instanceof PickaxeItem || item instanceof ShovelItem)
					levelList = LevelLists.toolList;
				if (levelList != null)
					if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((TieredItem) item).getTier().toString().toLowerCase(), true))
						return zero ? 0 : 1.0F;
			}
		}
		return original;
	}

	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V", shift = Shift.AFTER), cancellable = true)
	private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
		if (playerStatsManager.getSkillLevel(Skill.DEFENSE) >= ConfigInit.CONFIG.maxLevel && source.getEntity() != null
				&& playerEntity.level().getRandom().nextFloat() <= ConfigInit.CONFIG.defenseReflectChance) {
			source.getEntity().hurt(source, amount);
		}
		if (playerStatsManager.getSkillLevel(Skill.AGILITY) >= ConfigInit.CONFIG.maxLevel && playerEntity.level().getRandom().nextFloat() <= ConfigInit.CONFIG.movementMissChance) {
			info.setReturnValue(false);
		}
	}

	@Inject(method = "eat", at = @At(value = "HEAD"))
	private void eatFoodMixin(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
		if (stack.getItem().isEdible() && playerStatsManager.getSkillLevel(Skill.STAMINA) >= ConfigInit.CONFIG.maxLevel) {
			FoodProperties foodComponent = stack.getItem().getFoodProperties();
			float multiplier = ConfigInit.CONFIG.staminaFoodBonus;
			playerEntity.getFoodData().eat((int) (foodComponent.getNutrition() * multiplier), foodComponent.getSaturationModifier() * multiplier);
		}
	}

	@Override
	public PlayerStatsManager getPlayerStatsManager() {
		return this.playerStatsManager;
	}

	@Override
	public void increaseKilledMobStat(ChunkAccess chunk) {
		if (killedMobChunk != null && killedMobChunk == chunk) {
			killedMobsInChunk++;
		} else {
			killedMobChunk = chunk;
			killedMobsInChunk = 0;
		}
	}

	@Override
	public void resetKilledMobStat() {
		killedMobsInChunk = 0;
	}

	@Override
	public boolean allowMobDrop() {
		return killedMobsInChunk >= ConfigInit.CONFIG.mobKillCount ? false : true;
	}

	@Override
	protected void dropExperience() {
		if (this.level() instanceof ServerLevel
				&& (this.isAlwaysExperienceDropper() || this.lastHurtByPlayerTime > 0 && this.shouldDropExperience() && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
			if (ConfigInit.CONFIG.dropPlayerXP && (ConfigInit.CONFIG.resetCurrentXP || ConfigInit.CONFIG.hardMode))
				LevelExperienceOrbEntity.spawn((ServerLevel) this.level(), this.position(), (int) (playerStatsManager.getLevelProgress() * playerStatsManager.getNextLevelExperience()));
			ExperienceOrb.award((ServerLevel) this.level(), this.position(), this.getExperienceReward());
		}
	}
}
