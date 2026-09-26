package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.truesurvivalhelper.TshItemModelSides;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin {
	@Inject(method = "createSideElements", at = @At("HEAD"), cancellable = true)
	private void tsh$createSideElements(SpriteContents contents, String texture, int tintIndex, CallbackInfoReturnable<List<BlockElement>> cir) {
		cir.setReturnValue(TshItemModelSides.create(contents, texture, tintIndex));
	}
}
