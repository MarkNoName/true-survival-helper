package net.levelz.entity.render;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class LevelExperienceOrbEntityRenderer extends EntityRenderer<LevelExperienceOrbEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/experience_orb.png");
	private static final RenderType LAYER = RenderType.itemEntityTranslucentCull(TEXTURE);

	public LevelExperienceOrbEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.15f;
		this.shadowStrength = 0.75f;
	}

	@Override
	protected int getBlockLightLevel(LevelExperienceOrbEntity experienceOrbEntity, BlockPos blockPos) {
		return Mth.clamp(super.getBlockLightLevel(experienceOrbEntity, blockPos) + 7, 0, 15);
	}

	@Override
	public void render(LevelExperienceOrbEntity experienceOrbEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
		matrixStack.pushPose();
		int j = experienceOrbEntity.getOrbSize();
		float h = (float) (j % 4 * 16 + 0) / 64.0f;
		float k = (float) (j % 4 * 16 + 16) / 64.0f;
		float l = (float) (j / 4 * 16 + 0) / 64.0f;
		float m = (float) (j / 4 * 16 + 16) / 64.0f;

		int s = 63;
		int t = 201;
		int u = 255;

		matrixStack.translate(0.0, 0.1f, 0.0);
		matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));

		matrixStack.scale(0.3f, 0.3f, 0.3f);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
		PoseStack.Pose entry = matrixStack.last();
		Matrix4f matrix4f = entry.pose();
		Matrix3f matrix3f = entry.normal();
		LevelExperienceOrbEntityRenderer.vertex(vertexConsumer, matrix4f, matrix3f, -0.5f, -0.25f, s, t, u, h, m, i);
		LevelExperienceOrbEntityRenderer.vertex(vertexConsumer, matrix4f, matrix3f, 0.5f, -0.25f, s, t, u, k, m, i);
		LevelExperienceOrbEntityRenderer.vertex(vertexConsumer, matrix4f, matrix3f, 0.5f, 0.75f, s, t, u, k, l, i);
		LevelExperienceOrbEntityRenderer.vertex(vertexConsumer, matrix4f, matrix3f, -0.5f, 0.75f, s, t, u, h, l, i);
		matrixStack.popPose();
		super.render(experienceOrbEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	private static void vertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
		vertexConsumer.vertex(positionMatrix, x, y, 0.0f).color(red, green, blue, 128).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).endVertex();
	}

	@Override
	public ResourceLocation getTextureLocation(LevelExperienceOrbEntity experienceOrbEntity) {
		return TEXTURE;
	}
}
