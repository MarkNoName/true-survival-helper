package net.truesurvivalhelper.mixin.client;

import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.serialization.Codec;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

import net.truesurvivalhelper.GuiBackportChunkFadeConfig;
import net.truesurvivalhelper.GuiBackportChunkFadeOptionAccess;

@Mixin(Options.class)
public abstract class OptionsMixin implements GuiBackportChunkFadeOptionAccess {
	@Unique
	private final OptionInstance<Double> guibackport$chunkFadeTime = new OptionInstance<>(
		"options.chunkFade",
		OptionInstance.cachedConstantTooltip(Component.translatable("options.chunkFade.tooltip")),
		(component, value) -> value <= 0.0
			? Component.translatable("options.chunkFade.none")
			: Component.translatable("options.chunkFade.seconds", String.format(Locale.ROOT, "%.2f", value)),
		new OptionInstance.IntRange(0, 40).xmap(i -> i / 20.0, value -> (int) (value * 20.0)),
		Codec.doubleRange(0.0, 2.0),
		GuiBackportChunkFadeConfig.load(),
		GuiBackportChunkFadeConfig::save
	);

	@Override
	public OptionInstance<Double> guibackport$chunkFadeTime() {
		return this.guibackport$chunkFadeTime;
	}
}
