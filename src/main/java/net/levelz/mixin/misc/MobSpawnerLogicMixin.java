package net.levelz.mixin.misc;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.access.MobEntityAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;

@Mixin(BaseSpawner.class)
public class MobSpawnerLogicMixin {
	@Inject(method = "serverTick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Mob;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	private void serverTickMixin(ServerLevel world, BlockPos pos, CallbackInfo info, boolean bl, RandomSource random, SpawnData mobSpawnerEntry, int i, CompoundTag nbtCompound, Optional<EntityType<?>> optional,
			ListTag nbtList, int j, double d, double e, double f, BlockPos blockPos, Entity entity) {
		((MobEntityAccess) entity).setSpawnerMob(true);
	}
}
