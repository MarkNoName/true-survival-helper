package net.truesurvivalhelper.bloodmoon;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.truesurvivalhelper.TrueSurvivalHelper;

/**
 * Orchestrates the Blood Moon event: day counting, the 10-day/5%-chance schedule, night-only
 * activation, world-state persistence, client sync, mob-spawning delegation and the sleep block.
 *
 * True Survival Helper's "feels like vanilla" design rule means this event is otherwise
 * completely silent - {@link #onAllowSleeping} is the one deliberate exception, per explicit
 * user direction, to give sleep denial its own themed message instead of vanilla's generic one.
 */
public final class BloodMoonManager {
	private static final long MIN_GAP_DAYS = 10;
	private static final double TRIGGER_CHANCE = 0.05;
	private static final long TICKS_PER_DAY = 24000L;
	/** Matches vanilla's own "/time set night" preset value. */
	private static final long NIGHT_TICK = 13000L;

	private static final Component SLEEP_DENIED_MESSAGE = Component.literal("You may not rest now, the blood moon is rising");

	private BloodMoonManager() {
	}

	public static void register() {
		ServerTickEvents.END_WORLD_TICK.register(BloodMoonManager::onEndWorldTick);
		EntitySleepEvents.ALLOW_SLEEPING.register(BloodMoonManager::onAllowSleeping);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> syncNewPlayer(handler.getPlayer()));
		TrueSurvivalHelper.LOGGER.info("Blood Moon event registered (min {} days apart, {}% chance/day).", MIN_GAP_DAYS, TRIGGER_CHANCE * 100);
	}

	public static boolean isActive(Level level) {
		if (!(level instanceof ServerLevel serverLevel) || serverLevel.dimension() != Level.OVERWORLD) {
			return false;
		}
		return BloodMoonSavedData.get(serverLevel).isActive();
	}

	/**
	 * Used by {@code /event blood_moon trigger} - forces activation regardless of time of day, by
	 * also forcing it to be night if it isn't already. Forcing activation alone during the day
	 * used to get silently undone within one tick: {@link #onEndWorldTick}'s deactivate check
	 * (active && isDay()) would immediately fire right after, since nothing had actually made it
	 * night. Jumping only the time-of-day portion forward (never the day number itself) keeps the
	 * persisted day counter - and the 10-day gap it enforces - untouched.
	 */
	public static void forceTrigger(ServerLevel overworld) {
		forceNight(overworld);
		BloodMoonSavedData data = BloodMoonSavedData.get(overworld);
		data.setLastBloodMoonDay(currentDay(overworld));
		data.setActive(true);
		syncAll(overworld.getServer(), true);
	}

	private static void forceNight(ServerLevel overworld) {
		long dayTime = overworld.getDayTime();
		long timeOfDay = dayTime % TICKS_PER_DAY;
		if (timeOfDay < NIGHT_TICK) {
			overworld.setDayTime(dayTime - timeOfDay + NIGHT_TICK);
		}
	}

	private static void onEndWorldTick(ServerLevel level) {
		if (level.dimension() != Level.OVERWORLD) {
			return;
		}
		BloodMoonSavedData data = BloodMoonSavedData.get(level);
		long currentDay = currentDay(level);

		if (currentDay != data.getLastRollDay()) {
			data.setLastRollDay(currentDay);
			if (!data.isActive() && currentDay - data.getLastBloodMoonDay() >= MIN_GAP_DAYS
					&& level.getRandom().nextDouble() < TRIGGER_CHANCE) {
				data.setScheduledForDay(currentDay);
			}
		}

		if (!data.isActive() && !level.isDay() && data.getScheduledForDay() == currentDay) {
			data.setActive(true);
			data.setLastBloodMoonDay(currentDay);
			syncAll(level.getServer(), true);
		} else if (data.isActive() && level.isDay()) {
			data.setActive(false);
			syncAll(level.getServer(), false);
		}

		if (data.isActive()) {
			BloodMoonSpawner.tick(level);
		}
	}

	private static long currentDay(ServerLevel level) {
		return level.getDayTime() / TICKS_PER_DAY;
	}

	private static Player.BedSleepingProblem onAllowSleeping(Player player, BlockPos pos) {
		if (!isActive(player.level())) {
			return null;
		}
		player.displayClientMessage(SLEEP_DENIED_MESSAGE, true);
		return Player.BedSleepingProblem.OTHER_PROBLEM;
	}

	private static void syncNewPlayer(ServerPlayer player) {
		ServerLevel overworld = player.getServer().overworld();
		boolean active = BloodMoonSavedData.get(overworld).isActive();
		ServerPlayNetworking.send(player, BloodMoonNetworking.CHANNEL, BloodMoonNetworking.write(active));
	}

	private static void syncAll(MinecraftServer server, boolean active) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ServerPlayNetworking.send(player, BloodMoonNetworking.CHANNEL, BloodMoonNetworking.write(active));
		}
	}
}
