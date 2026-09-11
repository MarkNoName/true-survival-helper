package net.truesurvivalhelper.bloodmoon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Persisted per-Overworld state for the Blood Moon event: when it last happened, whether
 * today's roll has already been made, whether one is armed for tonight, and whether one is
 * currently active.
 */
public class BloodMoonSavedData extends SavedData {
	private static final String ID = "tsh_blood_moon";

	private long lastBloodMoonDay = 0;
	private long scheduledForDay = -1;
	private long lastRollDay = -1;
	private boolean active = false;

	public static BloodMoonSavedData get(ServerLevel overworld) {
		return overworld.getDataStorage().computeIfAbsent(BloodMoonSavedData::load, BloodMoonSavedData::new, ID);
	}

	public static BloodMoonSavedData load(CompoundTag tag) {
		BloodMoonSavedData data = new BloodMoonSavedData();
		data.lastBloodMoonDay = tag.getLong("lastBloodMoonDay");
		data.scheduledForDay = tag.contains("scheduledForDay") ? tag.getLong("scheduledForDay") : -1;
		data.lastRollDay = tag.contains("lastRollDay") ? tag.getLong("lastRollDay") : -1;
		data.active = tag.getBoolean("active");
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		tag.putLong("lastBloodMoonDay", lastBloodMoonDay);
		tag.putLong("scheduledForDay", scheduledForDay);
		tag.putLong("lastRollDay", lastRollDay);
		tag.putBoolean("active", active);
		return tag;
	}

	public long getLastBloodMoonDay() {
		return lastBloodMoonDay;
	}

	public void setLastBloodMoonDay(long day) {
		this.lastBloodMoonDay = day;
		setDirty();
	}

	public long getScheduledForDay() {
		return scheduledForDay;
	}

	public void setScheduledForDay(long day) {
		this.scheduledForDay = day;
		setDirty();
	}

	public long getLastRollDay() {
		return lastRollDay;
	}

	public void setLastRollDay(long day) {
		this.lastRollDay = day;
		setDirty();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		setDirty();
	}
}
