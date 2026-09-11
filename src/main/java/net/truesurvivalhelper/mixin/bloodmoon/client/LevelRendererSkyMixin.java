package net.truesurvivalhelper.mixin.bloodmoon.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.truesurvivalhelper.bloodmoon.client.BloodMoonClientState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Tints the sky disc and the moon red while a Blood Moon is active, fading smoothly via
 * {@link BloodMoonClientState#getBlend()}, both intercepted directly inside renderSky rather than
 * only through ClientLevel#getSkyColor. The sky "disc" vanilla draws (a flat mesh at a fixed
 * height above the camera, per buildSkyDisc/createLightSky) is a separate layer from the
 * starfield behind it - testing showed getSkyColor's own override alone left a visible gap (stars
 * still on their normal dark background) even though getSkyColor's return value should reach this
 * same draw call. Setting the color directly at the exact RenderSystem call vanilla uses for this
 * mesh (ordinal 0 - the very first setShaderColor call in the method) mirrors the moon fix below,
 * which is confirmed working in-game.
 */
@Mixin(LevelRenderer.class)
public abstract class LevelRendererSkyMixin {
	@Shadow
	@Final
	private static ResourceLocation MOON_LOCATION;

	@ModifyArgs(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 0))
	private void tsh$tintSkyDisc(Args args) {
		float blend = BloodMoonClientState.getBlend();
		if (blend <= 0F) {
			return;
		}
		float r = args.get(0);
		float g = args.get(1);
		float b = args.get(2);
		args.set(0, Mth.lerp(blend, r, BloodMoonClientState.SKY_RED));
		args.set(1, Mth.lerp(blend, g, BloodMoonClientState.SKY_GREEN));
		args.set(2, Mth.lerp(blend, b, BloodMoonClientState.SKY_BLUE));
	}

	@Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"))
	private void tsh$tintMoon(int index, ResourceLocation location) {
		float blend = BloodMoonClientState.getBlend();
		if (location.equals(MOON_LOCATION) && blend > 0F) {
			float[] color = RenderSystem.getShaderColor();
			float tint = Mth.lerp(blend, 1.0F, BloodMoonClientState.MOON_TINT);
			RenderSystem.setShaderColor(color[0], color[1] * tint, color[2] * tint, color[3]);
		}
		RenderSystem.setShaderTexture(index, location);
	}
}
