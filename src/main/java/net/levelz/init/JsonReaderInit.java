package net.levelz.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.levelz.data.LevelLoader;
import net.levelz.network.PlayerStatsServerPacket;
import net.minecraft.server.packs.PackType;

public class JsonReaderInit {
	public static final Logger LOGGER = LogManager.getLogger("LevelZ");

	public static void init() {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new LevelLoader());
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
			if (success) {
				for (int i = 0; i < server.getPlayerList().getPlayers().size(); i++)
					PlayerStatsServerPacket.writeS2CListPacket(server.getPlayerList().getPlayers().get(i));
				LOGGER.info("Finished reload on {}", Thread.currentThread());
			} else
				LOGGER.error("Failed to reload on {}", Thread.currentThread());
		});
	}
}
