package net.truesurvivalhelper;

import java.util.Arrays;

import com.mojang.serialization.Codec;

import dev.lambdaurora.lambdynlights.DynamicLightsConfig;
import dev.lambdaurora.lambdynlights.DynamicLightsMode;
import dev.lambdaurora.lambdynlights.LambDynLights;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public final class TshDynamicLights {
	private TshDynamicLights() {
	}

	public static OptionInstance<DynamicLightsMode> createOption() {
		DynamicLightsConfig config = LambDynLights.get().config;
		return new OptionInstance<>(
			"lambdynlights.option.mode",
			OptionInstance.cachedConstantTooltip(tooltip()),
			(caption, mode) -> label(mode),
			new OptionInstance.Enum<>(
				Arrays.asList(DynamicLightsMode.values()),
				Codec.STRING.xmap(id -> DynamicLightsMode.byId(id).orElse(DynamicLightsMode.FANCY), DynamicLightsMode::getName)
			),
			config.getDynamicLightsMode(),
			mode -> {
				config.setDynamicLightsMode(mode);
				config.save();
			}
		);
	}

	private static Component label(DynamicLightsMode mode) {
		return mode.getTranslatedText().plainCopy();
	}

	private static Component tooltip() {
		return Component.translatable("lambdynlights.tooltip.mode.1")
			.append("\n")
			.append(Component.translatable("lambdynlights.tooltip.mode.2", label(DynamicLightsMode.FASTEST), label(DynamicLightsMode.FAST)))
			.append("\n")
			.append(Component.translatable("lambdynlights.tooltip.mode.3", label(DynamicLightsMode.FANCY)));
	}
}
