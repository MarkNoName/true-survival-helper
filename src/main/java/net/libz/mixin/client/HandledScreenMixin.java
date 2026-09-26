package net.libz.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin extends Screen {
	@Shadow
	protected int leftPos;
	@Shadow
	protected int topPos;
	@Shadow
	@Nullable
	protected Slot hoveredSlot;

	public HandledScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void renderMixin(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo info) {
		DrawTabHelper.drawTab(minecraft, context, this, leftPos, topPos, mouseX, mouseY);
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	private void mouseClickedMixin(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
		DrawTabHelper.onTabButtonClick(minecraft, this, this.leftPos, this.topPos, mouseX, mouseY, this.hoveredSlot != null);
	}
}
