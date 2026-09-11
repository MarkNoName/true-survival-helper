package net.truesurvivalhelper.bloodmoon;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Supplemental spawner that adds extra hostile mobs near players while a Blood Moon is active.
 * Deliberately doesn't touch vanilla's own chunk/cap-based NaturalSpawner internals (complex,
 * private static, and unnecessary here) - it just adds mobs alongside unmodified vanilla
 * spawning, picking biome-appropriate types the same way vanilla itself would.
 */
final class BloodMoonSpawner {
	private static final int ATTEMPT_INTERVAL_TICKS = 100;
	private static final int MAX_PER_PLAYER = 9;
	private static final int MIN_RADIUS = 16;
	private static final int MAX_RADIUS = 32;
	private static final String SPAWN_TAG = "tsh:blood_moon";

	private BloodMoonSpawner() {
	}

	static void tick(ServerLevel level) {
		if (level.getGameTime() % ATTEMPT_INTERVAL_TICKS != 0) {
			return;
		}
		for (ServerPlayer player : level.players()) {
			trySpawnNear(level, player);
		}
	}

	private static void trySpawnNear(ServerLevel level, ServerPlayer player) {
		RandomSource random = level.getRandom();
		int nearby = level.getEntities(player, player.getBoundingBox().inflate(MAX_RADIUS * 2.0), e -> e.getTags().contains(SPAWN_TAG)).size();
		if (nearby >= MAX_PER_PLAYER) {
			return;
		}

		BlockPos pos = pickRingPosition(level, player.blockPosition(), random);
		if (pos == null) {
			return;
		}

		Holder<Biome> biome = level.getBiome(pos);
		Optional<MobSpawnSettings.SpawnerData> spawnerData = biome.value().getMobSettings().getMobs(MobCategory.MONSTER).getRandom(random);
		if (spawnerData.isEmpty()) {
			return;
		}

		EntityType<?> type = spawnerData.get().type;
		if (!SpawnPlacements.checkSpawnRules(type, level, MobSpawnType.NATURAL, pos, random)
				|| !NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(type), level, pos, type)) {
			return;
		}

		Entity entity = type.create(level, null, null, pos, MobSpawnType.NATURAL, false, false);
		if (!(entity instanceof Mob mob)) {
			return;
		}
		mob.addTag(SPAWN_TAG);
		level.addFreshEntity(mob);
	}

	private static BlockPos pickRingPosition(ServerLevel level, BlockPos center, RandomSource random) {
		double angle = random.nextDouble() * Math.PI * 2;
		int radius = MIN_RADIUS + random.nextInt(MAX_RADIUS - MIN_RADIUS + 1);
		int x = center.getX() + (int) Math.round(Math.cos(angle) * radius);
		int z = center.getZ() + (int) Math.round(Math.sin(angle) * radius);
		if (!level.hasChunk(x >> 4, z >> 4)) {
			return null;
		}
		int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
		return new BlockPos(x, y, z);
	}
}
