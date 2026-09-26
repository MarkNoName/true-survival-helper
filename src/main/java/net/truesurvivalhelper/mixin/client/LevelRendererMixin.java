package net.truesurvivalhelper.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;

import org.joml.Matrix4f;

import net.truesurvivalhelper.GuiBackportChunkFadeAccess;
import net.truesurvivalhelper.GuiBackportChunkFadeMath;
import net.truesurvivalhelper.GuiBackportChunkFadeOptionAccess;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	@Redirect(
		method = "renderChunkLayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk;getCompiledChunk()Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;"
		)
	)
	private ChunkRenderDispatcher.CompiledChunk guibackport$applyChunkFade(
		ChunkRenderDispatcher.RenderChunk renderChunk,
		RenderType renderType,
		PoseStack poseStack,
		double camX,
		double camY,
		double camZ,
		Matrix4f matrix4f
	) {
		ChunkRenderDispatcher.CompiledChunk compiled = renderChunk.getCompiledChunk();
		if (!compiled.isEmpty(renderType)) {
			Uniform visibility = RenderSystem.getShader().getUniform("ChunkVisibility");
			if (visibility != null) {
				visibility.set(guibackport$computeVisibility(renderChunk, camX, camY, camZ));
				visibility.upload();
			}
		}

		return compiled;
	}

	@Unique
	private static float guibackport$computeVisibility(ChunkRenderDispatcher.RenderChunk renderChunk, double camX, double camY, double camZ) {
		GuiBackportChunkFadeAccess access = (GuiBackportChunkFadeAccess) (Object) renderChunk;
		long now = System.currentTimeMillis();
		long uploadedTime = access.guibackport$getUploadedTime();
		if (uploadedTime == 0L) {
			uploadedTime = now;
			access.guibackport$setUploadedTime(now);
		}

		double durationSeconds = ((GuiBackportChunkFadeOptionAccess) (Object) Minecraft.getInstance().options).guibackport$chunkFadeTime().get();
		long durationMs = (long) (durationSeconds * 1000.0);

		BlockPos origin = renderChunk.getOrigin();
		double dx = origin.getX() + 8.0 - camX;
		double dy = origin.getY() + 8.0 - camY;
		double dz = origin.getZ() + 8.0 - camZ;
		double distanceSqr = dx * dx + dy * dy + dz * dz;

		return GuiBackportChunkFadeMath.computeVisibility(now - uploadedTime, durationMs, distanceSqr);
	}
}
