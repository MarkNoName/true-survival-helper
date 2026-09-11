package net.truesurvivalhelper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import net.truesurvivalhelper.bloodmoon.BloodMoonCommand;
import net.truesurvivalhelper.bloodmoon.BloodMoonManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrueSurvivalHelper implements ModInitializer {
	public static final String MOD_ID = "tsh";
	public static final Logger LOGGER = LoggerFactory.getLogger("True Survival Helper");

	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().isModLoaded("levelz")) {
			logCoverage("toughasnails", "canteens, water purifier, thermoregulator");
			logCoverage("biomemakeover", "enchanted totem, cladded armor");
			logCoverage("vanillabackport", "spears, copper tools/armor");

			ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new TshCustomItemRegistrar());
		} else {
			LOGGER.info("LevelZ is not installed - skipping LevelZ compatibility fixes.");
		}

		// Blood Moon has nothing to do with LevelZ, so it always initializes.
		BloodMoonManager.register();
		BloodMoonCommand.register();
	}

	private void logCoverage(String modId, String features) {
		if (FabricLoader.getInstance().isModLoaded(modId)) {
			LOGGER.info("Detected {} - enabling LevelZ compatibility for: {}", modId, features);
		} else {
			LOGGER.info("{} is not installed - skipping its LevelZ compatibility ({})", modId, features);
		}
	}
}
