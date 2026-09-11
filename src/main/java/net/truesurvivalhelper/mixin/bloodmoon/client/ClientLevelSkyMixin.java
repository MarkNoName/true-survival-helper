package net.truesurvivalhelper.mixin.bloodmoon.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.truesurvivalhelper.bloodmoon.client.BloodMoonClientState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Reddens the overworld sky and cloud color while a Blood Moon is active, fading smoothly via
 * {@link BloodMoonClientState#getBlend()} by lerping from whatever vanilla/Polytone already
 * computed toward the Blood Moon target, rather than switching instantly. Single return point in
 * both methods; Polytone wraps an internal sample call strictly upstream of getSkyColor's, and
 * doesn't touch getCloudColor at all, so no priority race is possible here regardless of mixin
 * order. Clouds have their own independent color method in vanilla (separate from sky/fog) -
 * missing this one left clouds their normal light gray, showing up as a stark seam against the
 * reddened sky in testing.
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelSkyMixin {
	@Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
	private void tsh$tintSky(CallbackInfoReturnable<Vec3> cir) {
		float blend = BloodMoonClientState.getBlend();
		if (blend <= 0F) {
			return;
		}
		Vec3 original = cir.getReturnValue();
		cir.setReturnValue(new Vec3(
			Mth.lerp(blend, (float) original.x, BloodMoonClientState.SKY_RED),
			Mth.lerp(blend, (float) original.y, BloodMoonClientState.SKY_GREEN),
			Mth.lerp(blend, (float) original.z, BloodMoonClientState.SKY_BLUE)));
	}

	@Inject(method = "getCloudColor", at = @At("RETURN"), cancellable = true)
	private void tsh$tintClouds(float partialTick, CallbackInfoReturnable<Vec3> cir) {
		float blend = BloodMoonClientState.getBlend();
		if (blend <= 0F) {
			return;
		}
		Vec3 original = cir.getReturnValue();
		cir.setReturnValue(new Vec3(
			Mth.lerp(blend, (float) original.x, BloodMoonClientState.CLOUD_RED),
			Mth.lerp(blend, (float) original.y, BloodMoonClientState.CLOUD_GREEN),
			Mth.lerp(blend, (float) original.z, BloodMoonClientState.CLOUD_BLUE)));
	}
}
