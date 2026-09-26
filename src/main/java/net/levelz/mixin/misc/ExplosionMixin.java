package net.levelz.mixin.misc;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;

import org.spongepowered.asm.mixin.injection.At;

import net.levelz.stats.PlayerStatsManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Explosion.class)
public class ExplosionMixin {
	@Shadow
	@Mutable
	@Final
	private Entity source;

	public ExplosionMixin(@Nullable Entity entity) {
		this.source = entity;
	}

	@ModifyVariable(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;immutable()Lnet/minecraft/core/BlockPos;", shift = Shift.BEFORE), ordinal = 0)
	private BlockState affectWorldMixin(BlockState original) {
		if (this.source != null) {
			if (this.source instanceof PrimedTnt && ((PrimedTnt) this.source).getOwner() != null && ((PrimedTnt) this.source).getOwner() instanceof Player) {
				if (PlayerStatsManager.listContainsItemOrBlock((Player) ((PrimedTnt) this.source).getOwner(), BuiltInRegistries.BLOCK.getId(original.getBlock()), 1)) {
					return Blocks.AIR.defaultBlockState();
				}
			}
		}
		return original;
	}
}
