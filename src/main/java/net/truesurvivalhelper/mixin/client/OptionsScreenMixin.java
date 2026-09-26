package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin {
	@Unique
	private static final Component TSH$VIDEO_LABEL = Component.translatable("options.video");

	@Shadow
	@Final
	private Options options;

	@Inject(method = "init", at = @At("TAIL"))
	private void tsh$forceVanillaVideoSettings(CallbackInfo ci) {
		ScreenAccessor self = (ScreenAccessor) (Object) this;
		OptionsScreen thisScreen = (OptionsScreen) (Object) this;
		AbstractWidget videoButton = null;
		for (GuiEventListener listener : self.tsh$children()) {
			if (listener instanceof AbstractWidget widget && widget.getMessage().equals(TSH$VIDEO_LABEL)) {
				videoButton = widget;
				break;
			}
		}
		if (videoButton == null) {
			return;
		}
		int x = videoButton.getX();
		int y = videoButton.getY();
		int width = videoButton.getWidth();
		int height = videoButton.getHeight();
		self.tsh$children().remove(videoButton);
		self.tsh$renderables().remove(videoButton);
		self.tsh$narratables().remove(videoButton);
		Button replacement = Button.builder(TSH$VIDEO_LABEL, button -> self.tsh$minecraft().setScreen(new VideoSettingsScreen(thisScreen, this.options)))
				.bounds(x, y, width, height)
				.build();
		self.tsh$children().add(replacement);
		self.tsh$renderables().add(replacement);
		self.tsh$narratables().add(replacement);
	}
}
