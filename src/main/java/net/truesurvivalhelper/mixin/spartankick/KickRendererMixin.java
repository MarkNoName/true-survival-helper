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
