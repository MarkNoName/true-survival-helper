package net.truesurvivalhelper.mixin.bloodmoon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.truesurvivalhelper.bloodmoon.BloodMoonManager;
import net.truesurvivalhelper.bloodmoon.BloodMoonMobBuffs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Toughens up (and sometimes arms) hostile mobs spawned while a Blood Moon is active - covers
 * both vanilla's own natural spawns and TSH's own supplemental Blood Moon spawner, since both
 * paths call this same vanilla method.
 */
@Mixin(Mob.class)
public abstract class MobFinalizeSpawnMixin {
	@Inject(method = "finalizeSpawn", at = @At("TAIL"))
	private void tsh$applyBloodMoonBuffs(ServerLevelAccessor accessor, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData groupData, CompoundTag tag, CallbackInfoReturnable<SpawnGroupData> cir) {
		// accessor is a WorldGenRegion (not a ServerLevel) during chunk generation - never assume it's a ServerLevel.
		if (!(accessor instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD || !BloodMoonManager.isActive(level)) {
			return;
		}
		Mob self = (Mob) (Object) this;
		if (self instanceof Enemy) {
			BloodMoonMobBuffs.apply(self, level.getRandom());
		}
	}
}
