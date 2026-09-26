package net.truesurvivalhelper.mixin.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SkinCustomizationScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.entity.player.PlayerModelPart;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.truesurvivalhelper.GuiBackportSimpleOptionsList;

@Mixin(SkinCustomizationScreen.class)
public abstract class SkinCustomizationScreenMixin {
	@Inject(method = "init", at = @At("HEAD"), cancellable = true)
	private void guibackport$useOptionsList(CallbackInfo ci) {
		Screen self = (Screen) (Object) this;
		ScreenAccessor screenAccess = (ScreenAccessor) (Object) this;
		OptionsSubScreenAccessor optionsAccess = (OptionsSubScreenAccessor) (Object) this;

		Minecraft minecraft = screenAccess.guibackport$minecraft();
		Options options = optionsAccess.guibackport$options();
		Screen lastScreen = optionsAccess.guibackport$lastScreen();
		int width = self.width;
		int height = self.height;

		List<AbstractWidget> widgets = new ArrayList<>();
		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			widgets.add(
				CycleButton.onOffBuilder(options.isModelPartEnabled(playerModelPart))
					.create(0, 0, 150, 20, playerModelPart.getName(), (cycleButton, value) -> options.toggleModelPart(playerModelPart, value))
			);
		}

		widgets.add(options.mainHand().createButton(options, 0, 0, 150));

		GuiBackportSimpleOptionsList list = new GuiBackportSimpleOptionsList(minecraft, width, height, 32, height - 32);
		list.addPairedRows(widgets);
		screenAccess.guibackport$addRenderableWidget(list);

		screenAccess.guibackport$addRenderableWidget(
			Button.builder(CommonComponents.GUI_DONE, button -> minecraft.setScreen(lastScreen)).bounds(width / 2 - 100, height - 27, 200, 20).build()
		);

		ci.cancel();
	}
}
