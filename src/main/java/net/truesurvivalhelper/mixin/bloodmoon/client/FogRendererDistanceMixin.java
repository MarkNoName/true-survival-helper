package net.truesurvivalhelper.mixin.bloodmoon.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.truesurvivalhelper.bloodmoon.client.BloodMoonClientState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FogRenderer.class, priority = 2000)
public abstract class FogRendererDistanceMixin {
	@Inject(method = "setupFog", at = @At("TAIL"))
	private static void tsh$tintFogDistance(CallbackInfo ci) {
		float blend = BloodMoonClientState.getBlend();
		if (blend <= 0F) {
			return;
		}
		float start = RenderSystem.getShaderFogStart();
		float end = RenderSystem.getShaderFogEnd();
		RenderSystem.setShaderFogStart(Mth.lerp(blend, start, BloodMoonClientState.FOG_START));
		RenderSystem.setShaderFogEnd(Mth.lerp(blend, end, BloodMoonClientState.FOG_END));
	}
}
