package net.truesurvivalhelper.mixin.bloodmoon.client;

import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.truesurvivalhelper.bloodmoon.client.BloodMoonClientState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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
