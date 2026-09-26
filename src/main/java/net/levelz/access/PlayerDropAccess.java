package net.levelz.access;

import net.minecraft.world.level.chunk.ChunkAccess;

public interface PlayerDropAccess {
	public void increaseKilledMobStat(ChunkAccess chunk);

	public boolean allowMobDrop();

	public void resetKilledMobStat();
}
