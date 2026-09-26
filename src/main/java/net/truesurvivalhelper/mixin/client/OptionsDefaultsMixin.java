package net.truesurvivalhelper.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.tutorial.TutorialSteps;
import net.truesurvivalhelper.TshDefaultOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsDefaultsMixin {
	@Shadow
	@Final
	private OptionInstance<Boolean> enableVsync;

	@Shadow
	@Final
	private OptionInstance<Boolean> realmsNotifications;

	@Shadow
	@Final
	private OptionInstance<Boolean> fullscreen;

	@Shadow
	@Final
	private OptionInstance<Boolean> darkMojangStudiosBackground;

	@Shadow
	@Final
	private OptionInstance<Integer> renderDistance;

	@Shadow
	@Final
	private OptionInstance<Integer> simulationDistance;

	@Shadow
	@Final
	private OptionInstance<Integer> guiScale;

	@Shadow
	@Final
	private OptionInstance<GraphicsStatus> graphicsMode;

	@Shadow
	public TutorialSteps tutorialStep;

	@Shadow
	public boolean hideBundleTutorial;

	@Shadow
	public String languageCode;

	@Shadow
	@Final
	public KeyMapping[] keyMappings;

	@Inject(method = "load", at = @At("HEAD"))
	private void tsh$applyDefaultOptions(CallbackInfo ci) {
		tsh$applyBoolean("enableVsync", this.enableVsync);
		tsh$applyBoolean("realmsNotifications", this.realmsNotifications);
		tsh$applyBoolean("fullscreen", this.fullscreen);
		tsh$applyBoolean("darkMojangStudiosBackground", this.darkMojangStudiosBackground);
		tsh$applyInt("renderDistance", this.renderDistance);
		tsh$applyInt("simulationDistance", this.simulationDistance);
		tsh$applyInt("guiScale", this.guiScale);

		String graphicsModeValue = TshDefaultOptions.get("graphicsMode");
		if (graphicsModeValue != null) {
			this.graphicsMode.set(GraphicsStatus.byId(Integer.parseInt(graphicsModeValue)));
		}

		String languageCodeValue = TshDefaultOptions.get("lang");
		if (languageCodeValue != null) {
			this.languageCode = languageCodeValue;
		}

		String tutorialStepValue = TshDefaultOptions.get("tutorialStep");
		if (tutorialStepValue != null) {
			this.tutorialStep = TutorialSteps.getByName(tutorialStepValue);
		}

		String hideBundleTutorialValue = TshDefaultOptions.get("hideBundleTutorial");
		if (hideBundleTutorialValue != null) {
			this.hideBundleTutorial = Boolean.parseBoolean(hideBundleTutorialValue);
		}

		for (KeyMapping keyMapping : this.keyMappings) {
			String value = TshDefaultOptions.get("key_" + keyMapping.getName());
			if (value != null) {
				keyMapping.setKey(InputConstants.getKey(value));
			}
		}
	}

	@Unique
	private static void tsh$applyBoolean(String key, OptionInstance<Boolean> option) {
		String value = TshDefaultOptions.get(key);
		if (value != null) {
			option.set(Boolean.parseBoolean(value));
		}
	}

	@Unique
	private static void tsh$applyInt(String key, OptionInstance<Integer> option) {
		String value = TshDefaultOptions.get(key);
		if (value != null) {
			option.set(Integer.parseInt(value));
		}
	}
}
