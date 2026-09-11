package net.truesurvivalhelper;

import net.fabricmc.api.ClientModInitializer;
import net.truesurvivalhelper.bloodmoon.client.BloodMoonClientState;
import net.truesurvivalhelper.bloodmoon.client.BloodMoonSounds;
import net.truesurvivalhelper.client.DenialOverlayRenderer;

public class TrueSurvivalHelperClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		DenialOverlayRenderer.register();
		BloodMoonSounds.register();
		BloodMoonClientState.register();
	}
}
