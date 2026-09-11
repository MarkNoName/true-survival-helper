package net.truesurvivalhelper.mixin.bloodmoon.client;

import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.truesurvivalhelper.bloodmoon.client.BloodMoonClientState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Reddens fog color while a Blood Moon is active, fading smoothly via
 * {@link BloodMoonClientState#getBlend()} rather than switching instantly - lerping from
 * whatever vanilla/Polytone already computed for this call toward the Blood Moon target means
 * blend=0 is indistinguishable from untouched, no separate "inactive" branch needed.
 *
 * Deliberately no ordinal on the clearColor target - setupColor has two
 * RenderSystem.clearColor(FFFF) call sites (a special-case branch and the true tail), and
 * matching both is exactly what's wanted (red fog regardless of which branch produced the base
 * color). Polytone's own biome-fog mixin only wraps an internal color-sample call strictly
 * upstream of both, so there's no ordering/priority concern here.
 *
 * clearColor only governs the background/void clear color (why the sky/horizon already looked
 * red in testing) - it is NOT what actual rendered terrain fades toward as it fogs out. That's a
 * completely separate call, RenderSystem.setShaderFogColor(FFF) (no alpha) inside the small
 * standalone FogRenderer#levelFogColor() method - missing this meant terrain never actually
 * faded to red, only the backdrop behind it did.
 */
@Mixin(FogRenderer.class)
public abstract class FogRendererColorMixin {
	@ModifyArgs(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"))
	private static void tsh$tintClearColor(Args args) {
		float blend = BloodMoonClientState.getBlend();
		if (blend <= 0F) {
			return;
		}
		float r = args.get(0);
		float g = args.get(1);
		float b = args.get(2);
		args.set(0, Mth.lerp(blend, r, BloodMoonClientState.FOG_RED));
		args.set(1, Mth.lerp(blend, g, BloodMoonClientState.FOG_GREEN));
		args.set(2, Mth.lerp(blend, b, BloodMoonClientState.FOG_BLUE));
	}

	@ModifyArgs(method = "levelFogColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogColor(FFF)V"))
	private static void tsh$tintTerrainFogColor(Args args) {
		float blend = BloodMoonClientState.getBlend();
		if (blend <= 0F) {
			return;
		}
		float r = args.get(0);
		float g = args.get(1);
		float b = args.get(2);
		args.set(0, Mth.lerp(blend, r, BloodMoonClientState.FOG_RED));
		args.set(1, Mth.lerp(blend, g, BloodMoonClientState.FOG_GREEN));
		args.set(2, Mth.lerp(blend, b, BloodMoonClientState.FOG_BLUE));
	}
}
