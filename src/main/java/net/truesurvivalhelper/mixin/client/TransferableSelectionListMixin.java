package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.gui.screens.packs.TransferableSelectionList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(TransferableSelectionList.class)
public abstract class TransferableSelectionListMixin {
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 55))
	private static int guibackport$extendListToMatchLoweredButtons(int original) {
		return 34;
	}
}
