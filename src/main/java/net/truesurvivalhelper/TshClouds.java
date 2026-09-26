package net.truesurvivalhelper;

import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class TshClouds {
	private static final ResourceLocation SHADER = new ResourceLocation("tsh", "clouds");
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/environment/clouds.png");

	private static final float CELL_SIZE = 12.0F;
	private static final float THICKNESS = 6.0F;
	private static final float ALTITUDE = 128.0F;
	private static final float ALTITUDE_OFFSET = 0.23F;
	private static final double DRIFT_SPEED = 0.03;
	private static final double DRIFT_OFFSET_Z = 3.96;
	private static final float OPACITY = 0.8F;
	private static final float FADE_OPACITY = 0.2F;
	private static final float FADE_RANGE = 10.0F;
	private static final float FOG_START = 50.0F;
	private static final float FOG_END = 600.0F;
	private static final float FLAT_FOG_END = 200.0F;
	private static final int REBUILD_DISTANCE = 13;

	private static ShaderInstance shader;
	private static Cells cells;
	private static VertexBuffer buffer;
	private static boolean empty = true;
	private static boolean dirty = true;
	private static boolean flat;
	private static int originX;
	private static int originZ;

	private TshClouds() {
	}

	public static void register() {
		CoreShaderRegistrationCallback.EVENT.register(context -> context.register(SHADER, DefaultVertexFormat.POSITION, program -> {
			shader = program;
			cells = null;
		}));
		DimensionRenderingRegistry.registerCloudRenderer(Level.OVERWORLD, TshClouds::render);
	}

	private static void render(WorldRenderContext context) {
		ClientLevel level = context.world();
		if (shader == null || level == null) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		if (cells == null) {
			cells = loadCells(minecraft.getResourceManager());
			dirty = true;
		}

		boolean fast = minecraft.options.getCloudsType() == CloudStatus.FAST;
		Vec3 camera = context.camera().getPosition();
		float partialTick = context.tickDelta();
		double time = (level.getGameTime() + partialTick) / 20.0;
		double x = camera.x + time * DRIFT_SPEED;
		double z = camera.z + DRIFT_OFFSET_Z + time * DRIFT_SPEED;
		int cellX = Mth.floor(x / CELL_SIZE);
		int cellZ = Mth.floor(z / CELL_SIZE);
		if (dirty || fast != flat || Math.abs(cellX - originX) > REBUILD_DISTANCE || Math.abs(cellZ - originZ) > REBUILD_DISTANCE) {
			originX = cellX;
			originZ = cellZ;
			flat = fast;
			dirty = false;
			rebuild();
		}

		if (empty) {
			return;
		}

		Vec3 color = level.getCloudColor(partialTick);
		shader.MODEL_VIEW_MATRIX.set(context.matrixStack().last().pose());
		shader.PROJECTION_MATRIX.set(context.projectionMatrix());
		shader.safeGetUniform("CloudOffset").set((float) (originX * (double) CELL_SIZE - x), (float) (ALTITUDE + ALTITUDE_OFFSET - camera.y), (float) (originZ * (double) CELL_SIZE - z));
		shader.safeGetUniform("CloudColor").set((float) color.x, (float) color.y, (float) color.z, flat ? OPACITY * (1.0F - FADE_OPACITY) : OPACITY);
		shader.safeGetUniform("CloudFade").set(flat ? 0.0F : FADE_OPACITY, FADE_RANGE, THICKNESS, (float) (ALTITUDE + THICKNESS / 2.0F - camera.y));
		shader.safeGetUniform("CloudFog").set(FOG_START, fogEnd());

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.depthMask(true);
		GL11.glEnable(GL32.GL_DEPTH_CLAMP);
		buffer.bind();
		shader.apply();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA,
			GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SourceFactor.ONE,
			GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
		);
		if (flat) {
			RenderSystem.disableCull();
			buffer.draw();
			RenderSystem.enableCull();
		} else {
			RenderSystem.enableCull();
			RenderSystem.colorMask(false, false, false, false);
			buffer.draw();
			RenderSystem.colorMask(true, true, true, true);
			buffer.draw();
		}
		shader.clear();
		VertexBuffer.unbind();
		GL11.glDisable(GL32.GL_DEPTH_CLAMP);
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	}

	private static float fogEnd() {
		return flat ? FLAT_FOG_END : FOG_END;
	}

	private static void rebuild() {
		int range = Mth.ceil(fogEnd() / CELL_SIZE) + REBUILD_DISTANCE + 1;
		BufferBuilder builder = Tesselator.getInstance().getBuilder();
		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
		for (int dz = -range; dz <= range; dz++) {
			for (int dx = -range; dx <= range; dx++) {
				if (cells.filled(originX + dx, originZ + dz)) {
					buildCell(builder, dx, dz);
				}
			}
		}

		BufferBuilder.RenderedBuffer rendered = builder.end();
		empty = rendered.isEmpty();
		if (empty) {
			rendered.release();
			return;
		}

		if (buffer == null) {
			buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		}
		buffer.bind();
		buffer.upload(rendered);
		VertexBuffer.unbind();
	}

	private static void buildCell(BufferBuilder builder, int dx, int dz) {
		float x0 = dx * CELL_SIZE;
		float x1 = x0 + CELL_SIZE;
		float z0 = dz * CELL_SIZE;
		float z1 = z0 + CELL_SIZE;

		quad(builder, x0, 0.0F, z0, x1, 0.0F, z0, x1, 0.0F, z1, x0, 0.0F, z1);
		if (flat) {
			return;
		}

		int cellX = originX + dx;
		int cellZ = originZ + dz;
		quad(builder, x0, THICKNESS, z1, x1, THICKNESS, z1, x1, THICKNESS, z0, x0, THICKNESS, z0);
		if (!cells.filled(cellX, cellZ + 1)) {
			quad(builder, x0, 0.0F, z1, x1, 0.0F, z1, x1, THICKNESS, z1, x0, THICKNESS, z1);
		}
		if (!cells.filled(cellX - 1, cellZ)) {
			quad(builder, x0, 0.0F, z0, x0, 0.0F, z1, x0, THICKNESS, z1, x0, THICKNESS, z0);
		}
		if (!cells.filled(cellX, cellZ - 1)) {
			quad(builder, x1, 0.0F, z0, x0, 0.0F, z0, x0, THICKNESS, z0, x1, THICKNESS, z0);
		}
		if (!cells.filled(cellX + 1, cellZ)) {
			quad(builder, x1, 0.0F, z1, x1, 0.0F, z0, x1, THICKNESS, z0, x1, THICKNESS, z1);
		}
	}

	private static void quad(BufferBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
		builder.vertex(x1, y1, z1).endVertex();
		builder.vertex(x2, y2, z2).endVertex();
		builder.vertex(x3, y3, z3).endVertex();
		builder.vertex(x4, y4, z4).endVertex();
	}

	private static Cells loadCells(ResourceManager resourceManager) {
		try (InputStream stream = resourceManager.open(TEXTURE); NativeImage image = NativeImage.read(stream)) {
			int width = image.getWidth();
			int height = image.getHeight();
			boolean[] mask = new boolean[width * height];
			for (int z = 0; z < height; z++) {
				for (int x = 0; x < width; x++) {
					mask[x + z * width] = (image.getPixelRGBA(x, z) >>> 24) >= 5;
				}
			}
			return new Cells(width, height, mask);
		} catch (IOException e) {
			TrueSurvivalHelper.LOGGER.warn("Failed to load cloud texture", e);
			return new Cells(1, 1, new boolean[1]);
		}
	}

	private record Cells(int width, int height, boolean[] mask) {
		boolean filled(int x, int z) {
			return this.mask[Math.floorMod(x, this.width) + Math.floorMod(z, this.height) * this.width];
		}
	}
}
