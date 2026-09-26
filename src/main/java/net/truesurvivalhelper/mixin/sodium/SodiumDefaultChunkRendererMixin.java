package net.truesurvivalhelper.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformBlock;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import me.jellysquid.mods.sodium.client.render.chunk.DefaultChunkRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.data.SectionRenderDataStorage;
import me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegion;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import me.jellysquid.mods.sodium.client.render.viewport.CameraTransform;

import net.minecraft.client.Minecraft;

import net.truesurvivalhelper.GuiBackportChunkFadeMath;
import net.truesurvivalhelper.GuiBackportChunkFadeOptionAccess;
import net.truesurvivalhelper.GuiBackportSodiumChunkShaderAccess;
import net.truesurvivalhelper.GuiBackportSodiumRegionAccess;
import net.truesurvivalhelper.GuiBackportSodiumSectionAccess;

@Mixin(value = DefaultChunkRenderer.class, remap = false)
public abstract class SodiumDefaultChunkRendererMixin {
	@Shadow
	private static void setModelMatrixUniforms(ChunkShaderInterface shader, RenderRegion region, CameraTransform camera) {
		throw new AssertionError("shadowed");
	}

	@Redirect(
		method = "fillCommandBuffer",
		at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/data/SectionRenderDataStorage;getDataPointer(I)J")
	)
	private static long guibackport$onGetDataPointer(
		SectionRenderDataStorage storage,
		int sectionIndex,
		me.jellysquid.mods.sodium.client.gl.device.MultiDrawBatch batch,
		RenderRegion renderRegion,
		SectionRenderDataStorage renderDataStorage,
		me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderList renderList,
		CameraTransform camera,
		me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass pass,
		boolean useBlockFaceCulling
	) {
		guibackport$updateSectionFade(renderRegion, sectionIndex, camera);
		return storage.getDataPointer(sectionIndex);
	}

	@Unique
	private static void guibackport$updateSectionFade(RenderRegion renderRegion, int sectionIndex, CameraTransform camera) {
		RenderSection section = renderRegion.getSection(sectionIndex);
		if (section == null) {
			return;
		}

		GuiBackportSodiumSectionAccess access = (GuiBackportSodiumSectionAccess) (Object) section;
		long now = System.currentTimeMillis();
		long firstBuilt = access.guibackport$getFirstBuiltTime();
		if (firstBuilt == 0L) {
			firstBuilt = now;
			access.guibackport$setFirstBuiltTime(now);
		}

		double durationSeconds = ((GuiBackportChunkFadeOptionAccess) (Object) Minecraft.getInstance().options).guibackport$chunkFadeTime().get();
		long durationMs = (long) (durationSeconds * 1000.0);

		double distanceSqr = section.getSquaredDistance(
			camera.intX + camera.fracX,
			camera.intY + camera.fracY,
			camera.intZ + camera.fracZ
		);

		float visibility = GuiBackportChunkFadeMath.computeVisibility(now - firstBuilt, durationMs, distanceSqr);
		((GuiBackportSodiumRegionAccess) (Object) renderRegion).guibackport$writeFadeVisibility(sectionIndex, visibility);
	}

	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lme/jellysquid/mods/sodium/client/render/chunk/DefaultChunkRenderer;setModelMatrixUniforms(Lme/jellysquid/mods/sodium/client/render/chunk/shader/ChunkShaderInterface;Lme/jellysquid/mods/sodium/client/render/chunk/region/RenderRegion;Lme/jellysquid/mods/sodium/client/render/viewport/CameraTransform;)V"
		)
	)
	private static void guibackport$setModelMatrixUniformsAndBindFade(
		ChunkShaderInterface shader,
		RenderRegion region,
		CameraTransform camera,
		ChunkRenderMatrices matrices,
		CommandList commandList
	) {
		setModelMatrixUniforms(shader, region, camera);

		GlUniformBlock block = ((GuiBackportSodiumChunkShaderAccess) (Object) shader).guibackport$getFadeVisibilityBlock();
		if (block != null) {
			((GuiBackportSodiumRegionAccess) (Object) region).guibackport$uploadAndBindFadeVisibility(commandList, block);
		}
	}
}
