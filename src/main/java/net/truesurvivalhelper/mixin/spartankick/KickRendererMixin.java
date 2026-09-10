package net.truesurvivalhelper.mixin.spartankick;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.spartankick.client.render.KickRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Spartan Kick's first-person leg/pants model is rendered with a hardcoded
 * full-bright packed light (0xF000F0) instead of the "light" parameter its own
 * caller (GameRendererMixin#onRenderHand) already computes correctly via
 * EntityRenderDispatcher#getPackedLightCoords - that's why the kicking foot
 * looks identically lit at noon and at midnight.
 *
 * Stash that already-correct value in a field at the head of render() (a plain
 * @Inject, which can freely see the enclosing method's own parameters), then
 * substitute it in on both ModelPart#render calls via @Redirect (which, unlike
 * @Inject, only ever sees the redirected call's own receiver+args - it has no
 * way to also reach into the enclosing method's parameters directly). Model,
 * pose and texture are untouched.
 */
@Mixin(KickRenderer.class)
public abstract class KickRendererMixin {
	@Unique
	private int tsh$light;

	@Inject(method = "render", at = @At("HEAD"))
	private void tsh$captureLight(PoseStack matrices, MultiBufferSource vertexConsumers, float kickProgress, AbstractClientPlayer player, int light, CallbackInfo ci) {
		this.tsh$light = light;
	}

	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"
		)
	)
	private void tsh$useDynamicLight(ModelPart instance, PoseStack matrices, VertexConsumer buffer, int packedLight, int packedOverlay) {
		instance.render(matrices, buffer, this.tsh$light, packedOverlay);
	}
}
