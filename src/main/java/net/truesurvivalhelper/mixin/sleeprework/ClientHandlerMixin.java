package net.truesurvivalhelper.mixin.sleeprework;

import dev.architectury.hooks.client.screen.ScreenAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import party.lemons.sleeprework.SleepRework;
import party.lemons.sleeprework.client.AbstractContainerScreenAccess;
import party.lemons.sleeprework.client.SleepDisplayWidget;
import party.lemons.sleeprework.handler.ClientHandler;

@Mixin(ClientHandler.class)
public abstract class ClientHandlerMixin {
	@Inject(method = "addSleepWidget", at = @At("HEAD"), cancellable = true)
	private static void tsh$trackScreenPosition(InventoryScreen inventoryScreen, ScreenAccess screen, CallbackInfo ci) {
		int x = ((AbstractContainerScreenAccess) inventoryScreen).getLeft() + SleepRework.CONFIG.clientConfig().iconX();
		int y = ((AbstractContainerScreenAccess) inventoryScreen).getTop() + SleepRework.CONFIG.clientConfig().iconY();
		SleepDisplayWidget widget = new SleepDisplayWidget(x, y) {
			@Override
			protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
				this.setX(((AbstractContainerScreenAccess) inventoryScreen).getLeft() + SleepRework.CONFIG.clientConfig().iconX());
				super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
			}
		};
		screen.addRenderableWidget(widget);
		ci.cancel();
	}
}
