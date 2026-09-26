package net.truesurvivalhelper.mixin.client;

import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import net.truesurvivalhelper.GuiBackportPanorama;
import net.truesurvivalhelper.TshPackInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
	@Unique
	private static final Component TSH$REALMS_BUTTON_LABEL = Component.translatable("menu.online");

	@Shadow
	@Nullable
	private RealmsNotificationsScreen realmsNotificationsScreen;

	@ModifyArg(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I"
			),
			index = 1
	)
	private String tsh$brandString(String original) {
		return TshPackInfo.BRAND;
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void tsh$removeRealmsButton(CallbackInfo ci) {
		this.realmsNotificationsScreen = null;
		ScreenAccessor self = (ScreenAccessor) (Object) this;
		AbstractWidget realmsButton = null;
		for (GuiEventListener listener : self.tsh$children()) {
			if (listener instanceof AbstractWidget widget && widget.getMessage().equals(TSH$REALMS_BUTTON_LABEL)) {
				realmsButton = widget;
				break;
			}
		}
		if (realmsButton == null) {
			return;
		}
		int realmsY = realmsButton.getY();
		self.tsh$children().remove(realmsButton);
		self.tsh$renderables().remove(realmsButton);
		self.tsh$narratables().remove(realmsButton);
		for (GuiEventListener listener : self.tsh$children()) {
			if (listener instanceof AbstractWidget widget && !(widget instanceof PlainTextButton) && widget.getY() > realmsY) {
				widget.setY(widget.getY() - 24);
			}
		}
	}

	@Redirect(
			method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PanoramaRenderer;render(FF)V")
	)
	private void guibackport$renderSharedPanorama(PanoramaRenderer panoramaRenderer, float deltaTime, float alpha) {
		GuiBackportPanorama.PANORAMA.render(deltaTime, alpha);
	}
}
