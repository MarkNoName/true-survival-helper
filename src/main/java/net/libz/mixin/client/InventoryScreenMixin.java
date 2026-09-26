package net.libz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements Tab {
	public InventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
		super(screenHandler, playerInventory, text);
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	private void mouseClickedMixin(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
		DrawTabHelper.onTabButtonClick(minecraft, this, this.leftPos, this.topPos, mouseX, mouseY, this.hoveredSlot != null);
	}

	@Inject(method = "renderBg", at = @At("TAIL"))
	protected void drawBackgroundMixin(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo info) {
		DrawTabHelper.drawTab(minecraft, context, this, leftPos, topPos, mouseX, mouseY);
	}
}
