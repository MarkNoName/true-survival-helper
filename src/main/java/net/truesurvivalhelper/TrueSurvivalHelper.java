package net.truesurvivalhelper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import net.truesurvivalhelper.bloodmoon.BloodMoonCommand;
import net.truesurvivalhelper.bloodmoon.BloodMoonManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrueSurvivalHelper implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("True Survival Helper");

	@Override
	public void onInitialize() {
		logCoverage("toughasnails", "canteens, water purifier, thermoregulator");
		logCoverage("biomemakeover", "enchanted totem, cladded armor");
		logCoverage("vanillabackport", "spears, copper tools/armor");

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new TshCustomItemRegistrar());

		BloodMoonManager.register();
		BloodMoonCommand.register();
		TshXpBottleLoot.register();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> TshWorldVersion.write(server.getWorldPath(LevelResource.ROOT)));
	}

	private void logCoverage(String modId, String features) {
		if (FabricLoader.getInstance().isModLoaded(modId)) {
			LOGGER.info("Detected {} - enabling LevelZ compatibility for: {}", modId, features);
		} else {
			LOGGER.info("{} is not installed - skipping its LevelZ compatibility ({})", modId, features);
		}
	}
}
