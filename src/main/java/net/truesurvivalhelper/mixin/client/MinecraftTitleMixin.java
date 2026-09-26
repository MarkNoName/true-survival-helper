package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.Minecraft;
import net.truesurvivalhelper.TshPackInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftTitleMixin {
	@Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
	private void tsh$createTitle(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue("True Survival" + TshPackInfo.VERSION_SUFFIX);
	}
}
