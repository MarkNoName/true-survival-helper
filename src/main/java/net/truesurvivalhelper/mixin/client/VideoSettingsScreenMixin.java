package net.truesurvivalhelper.mixin.client;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.truesurvivalhelper.GuiBackportChunkFadeOptionAccess;
import net.truesurvivalhelper.TshDynamicLights;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsScreenMixin {
	@Unique
	private static final Component TSH$SODIUM_BUTTON_LABEL = Component.translatable("text.bettersodiumvideosettings.sodiumvideosettings");

	@Unique
	private static final Component TSH$DYNAMIC_LIGHTS_SETTINGS_CAPTION = Component.translatable("lambdynlights.menu.title");

	@Unique
	private static final boolean TSH$DYNAMIC_LIGHTS_LOADED = FabricLoader.getInstance().isModLoaded("lambdynlights");

	@Redirect(
		method = "init",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall([Lnet/minecraft/client/OptionInstance;)V")
	)
	private void tsh$addVideoOptions(OptionsList list, OptionInstance<?>[] original) {
		Options options = Minecraft.getInstance().options;
		OptionInstance<Double> chunkFadeTime = ((GuiBackportChunkFadeOptionAccess) (Object) options).guibackport$chunkFadeTime();
		OptionInstance<?> dynamicLights = TSH$DYNAMIC_LIGHTS_LOADED ? TshDynamicLights.createOption() : null;

		List<OptionInstance<?>> result = new ArrayList<>(original.length + 2);
		boolean fadeInserted = false;
		boolean dynamicLightsInserted = dynamicLights == null;
		for (OptionInstance<?> option : original) {
			if (((OptionInstanceAccessor) (Object) option).tsh$caption().equals(TSH$DYNAMIC_LIGHTS_SETTINGS_CAPTION)) {
				continue;
			}
			result.add(option);
			if (!fadeInserted && option == options.ambientOcclusion()) {
				result.add(chunkFadeTime);
				fadeInserted = true;
			}
			if (!dynamicLightsInserted && option == options.fullscreen()) {
				result.add(dynamicLights);
				dynamicLightsInserted = true;
			}
		}

		if (!fadeInserted) {
			result.add(chunkFadeTime);
		}
		if (!dynamicLightsInserted) {
			result.add(dynamicLights);
		}

		list.addSmall(result.toArray(OptionInstance<?>[]::new));
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void tsh$restoreVanillaVideoScreen(CallbackInfo ci) {
		ScreenAccessor self = (ScreenAccessor) (Object) this;
		AbstractWidget sodiumButton = null;
		AbstractWidget doneButton = null;
		for (GuiEventListener listener : self.tsh$children()) {
			if (!(listener instanceof AbstractWidget widget)) {
				continue;
			}
			if (widget.getMessage().equals(TSH$SODIUM_BUTTON_LABEL)) {
				sodiumButton = widget;
			} else if (widget.getMessage().equals(CommonComponents.GUI_DONE)) {
				doneButton = widget;
			}
		}
		if (sodiumButton != null) {
			self.tsh$children().remove(sodiumButton);
			self.tsh$renderables().remove(sodiumButton);
			self.tsh$narratables().remove(sodiumButton);
		}
		if (doneButton != null) {
			doneButton.setX(self.tsh$width() / 2 - 100);
			doneButton.setWidth(200);
		}
	}
}
