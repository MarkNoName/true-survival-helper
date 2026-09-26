package net.truesurvivalhelper.mixin.sodium;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.jellysquid.mods.sodium.client.gl.buffer.GlBufferUsage;
import me.jellysquid.mods.sodium.client.gl.buffer.GlMutableBuffer;
import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformBlock;
import me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegion;

import net.truesurvivalhelper.GuiBackportSodiumRegionAccess;

@Mixin(value = RenderRegion.class, remap = false)
public abstract class SodiumRenderRegionMixin implements GuiBackportSodiumRegionAccess {
	@Unique
	private final ByteBuffer guibackport$fadeStaging = MemoryUtil.memAlloc(RenderRegion.REGION_SIZE * 16);

	@Unique
	private GlMutableBuffer guibackport$fadeBuffer;

	@Override
	public void guibackport$writeFadeVisibility(int sectionIndex, float visibility) {
		this.guibackport$fadeStaging.putFloat(sectionIndex * 16, visibility);
	}

	@Override
	public void guibackport$uploadAndBindFadeVisibility(CommandList commandList, GlUniformBlock uniformBlock) {
		if (this.guibackport$fadeBuffer == null) {
			this.guibackport$fadeBuffer = commandList.createMutableBuffer();
		}

		commandList.uploadData(this.guibackport$fadeBuffer, this.guibackport$fadeStaging, GlBufferUsage.STREAM_DRAW);
		uniformBlock.bindBuffer(this.guibackport$fadeBuffer);
	}

	@Inject(method = "delete", at = @At("TAIL"))
	private void guibackport$deleteFadeBuffer(CommandList commandList, CallbackInfo ci) {
		if (this.guibackport$fadeBuffer != null) {
			commandList.deleteBuffer(this.guibackport$fadeBuffer);
			this.guibackport$fadeBuffer = null;
		}

		MemoryUtil.memFree(this.guibackport$fadeStaging);
	}
}
