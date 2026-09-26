package net.levelz.mixin.entity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.Skill;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

@Mixin(Projectile.class)
public class ProjectileEntityMixin {
	@Shadow
	@Nullable
	private Entity cachedOwner;

	@ModifyArg(method = "shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;shoot(DDDFF)V", ordinal = 0), index = 4)
	private float setVelocityMixin(float original) {
		if (cachedOwner != null && cachedOwner instanceof Player playerEntity) {
			int archeryLevel = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager().getSkillLevel(Skill.ARCHERY);
			float newAccuracy = original - 0.3f + Math.abs(archeryLevel - ConfigInit.CONFIG.maxLevel) * ConfigInit.CONFIG.archeryInaccuracyBonus;
			if (archeryLevel < ConfigInit.CONFIG.maxLevel && newAccuracy < original && ConfigInit.CONFIG.archeryInaccuracyBonus > 0.001f) {
				return newAccuracy;
			}
		}
		return original;
	}
}
