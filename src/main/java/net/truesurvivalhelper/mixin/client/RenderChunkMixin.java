package net.truesurvivalhelper.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;

import net.truesurvivalhelper.GuiBackportChunkFadeAccess;

@Mixin(ChunkRenderDispatcher.RenderChunk.class)
public abstract class RenderChunkMixin implements GuiBackportChunkFadeAccess {
	@Unique
	private long guibackport$uploadedTime;

	@Override
	public long guibackport$getUploadedTime() {
		return this.guibackport$uploadedTime;
	}

	@Override
	public void guibackport$setUploadedTime(long time) {
		this.guibackport$uploadedTime = time;
	}

	@Inject(method = "reset", at = @At("TAIL"))
	private void guibackport$resetFadeTimer(CallbackInfo ci) {
		this.guibackport$uploadedTime = 0L;
	}
}
