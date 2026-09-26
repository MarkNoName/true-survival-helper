package net.truesurvivalhelper.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformBlock;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ShaderBindingContext;

import net.truesurvivalhelper.GuiBackportSodiumChunkShaderAccess;

@Mixin(value = ChunkShaderInterface.class, remap = false)
public abstract class SodiumChunkShaderInterfaceMixin implements GuiBackportSodiumChunkShaderAccess {
	private static final int GUIBACKPORT_FADE_UBO_BINDING = 2;

	@Unique
	private GlUniformBlock guibackport$fadeVisibilityBlock;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void guibackport$bindFadeUniformBlock(ShaderBindingContext context, ChunkShaderOptions options, CallbackInfo ci) {
		this.guibackport$fadeVisibilityBlock = context.bindUniformBlock("ubo_GuiBackportChunkFade", GUIBACKPORT_FADE_UBO_BINDING);
	}

	@Override
	public GlUniformBlock guibackport$getFadeVisibilityBlock() {
		return this.guibackport$fadeVisibilityBlock;
	}
}
