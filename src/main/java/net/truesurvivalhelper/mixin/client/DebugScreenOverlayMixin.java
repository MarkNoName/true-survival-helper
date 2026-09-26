package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.truesurvivalhelper.TshPackInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {
	@Inject(method = "getGameInformation", at = @At("RETURN"))
	private void tsh$replaceBrandLine(CallbackInfoReturnable<List<String>> cir) {
		List<String> list = cir.getReturnValue();
		if (!list.isEmpty()) {
			list.set(0, TshPackInfo.BRAND);
		}
	}
}
