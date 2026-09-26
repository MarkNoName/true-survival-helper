package net.levelz.mixin.entity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.Skill;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;

@Mixin(PrimedTnt.class)
public abstract class TntEntityMixin extends Entity {
	@Shadow
	@Nullable
	private LivingEntity owner;

	public TntEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Inject(method = "explode", at = @At(value = "HEAD"), cancellable = true)
	private void explodeMixin(CallbackInfo info) {
		if (owner != null && owner instanceof Player player) {
			if (((PlayerStatsManagerAccess) player).getPlayerStatsManager().getSkillLevel(Skill.MINING) >= ConfigInit.CONFIG.maxLevel) {
				this.level().explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 4.0F * (1F + ConfigInit.CONFIG.miningTntBonus), ExplosionInteraction.TNT);
				info.cancel();
			}
		}
	}
}
