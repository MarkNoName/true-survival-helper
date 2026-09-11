package net.truesurvivalhelper.mixin.bloodmoon.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.truesurvivalhelper.bloodmoon.client.BloodMoonClientState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pulls fog in close while a Blood Moon is active, fading smoothly via
 * {@link BloodMoonClientState#getBlend()} - reads whatever vanilla/Polytone already set for this
 * frame via the RenderSystem getters (already computed by the time this TAIL inject runs) and
 * lerps from there toward the Blood Moon target, rather than switching instantly.
 *
 * Polytone's own biome-fog-distance override (FogRendererMixin2) is itself a plain
 * @Inject(TAIL) on this same method rather than a modification of the original
 * setShaderFogStart/End call's arguments - Mixin priority only orders competing mixins at the
 * *same* injection point, so an @ModifyArgs on the original (earlier) call would still lose to
 * Polytone's separate, later TAIL write regardless of priority. Mirroring Polytone's own injector
 * type/position here, with a higher priority, is what actually guarantees this write happens last.
 */
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
