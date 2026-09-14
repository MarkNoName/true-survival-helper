package net.truesurvivalhelper.mixin.bloodmoon.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.truesurvivalhelper.bloodmoon.client.BloodMoonClientState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
