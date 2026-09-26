package net.levelz.mixin.entity;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Animal.class)
public abstract class AnimalEntityMixin extends AgeableMob {
	public AnimalEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;usePlayerItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V"), cancellable = true)
	private void interactMobMixin(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		ArrayList<Object> levelList = LevelLists.breedingList;
		if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, true)) {
			player.displayClientMessage(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED), true);
			info.setReturnValue(InteractionResult.FAIL);
		}
	}

	@Inject(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void breedMixin(ServerLevel world, Animal other, CallbackInfo info, AgeableMob passiveEntity) {
		if (getLoveCause() != null || other.getLoveCause() != null) {
			Player playerEntity = getLoveCause() != null ? getLoveCause() : other.getLoveCause();
			if (((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager().getSkillLevel(Skill.FARMING) >= ConfigInit.CONFIG.maxLevel
					&& world.random.nextFloat() < ConfigInit.CONFIG.farmingTwinChance) {
				AgeableMob extraPassiveEntity = this.getBreedOffspring(world, other);
				extraPassiveEntity.setBaby(true);
				extraPassiveEntity.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
				world.addFreshEntityWithPassengers(extraPassiveEntity);
			}
		}
	}

	@Inject(method = "finalizeSpawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;Lnet/minecraft/world/entity/AgeableMob;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
	private void breedExperienceMixin(ServerLevel world, Animal other, @Nullable AgeableMob baby, CallbackInfo info) {
		if (ConfigInit.CONFIG.breedingXPMultiplier > 0.0F)
			LevelExperienceOrbEntity.spawn(world, this.position().add(0.0D, 0.1D, 0.0D),
					(int) ((this.getRandom().nextInt(7) + 1) * ConfigInit.CONFIG.breedingXPMultiplier
							* (ConfigInit.CONFIG.dropXPbasedOnLvl && getLoveCause() != null
									? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) getLoveCause()).getPlayerStatsManager().getOverallLevel()
									: 1.0F)));
	}

	@Shadow
	@Nullable
	public ServerPlayer getLoveCause() {
		return null;
	}
}
