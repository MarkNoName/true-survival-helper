package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FaceBakery.class)
public abstract class FaceBakeryMixin {
	@Redirect(
			method = "bakeQuad",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;uvShrinkRatio()F"
			)
	)
	private float tsh$skipUvShrink(TextureAtlasSprite sprite) {
		return 0.0F;
	}
}
