package net.truesurvivalhelper.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.levelz.stats.Skill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public final class DenialOverlayRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tsh", "textures/gui/denial_box.png");
	private static final int TEX_NATIVE_WIDTH = 809;
	private static final int TEX_NATIVE_HEIGHT = 194;
	private static final int BOX_WIDTH = 250;
	private static final int BOX_HEIGHT = Math.round(BOX_WIDTH * (float) TEX_NATIVE_HEIGHT / TEX_NATIVE_WIDTH);
	private static final float BG_OPACITY_BOOST = 1.6F;

	private static final Component HEADER = Component.literal("You can't use this yet! You need:");

	private static final int HEADER_COLOR = 0xFFFF5555;
	private static final int BADGE_COLOR = 0xFFFFFFFF;
	private static final int CONTENT_GAP = 6;
	private static final float TOP_FRACTION = 0.13F;

	private DenialOverlayRenderer() {
	}

	public static void register() {
		HudRenderCallback.EVENT.register(DenialOverlayRenderer::render);
	}

	private static void render(GuiGraphics graphics, float tickDelta) {
		if (!DenialOverlayState.isActive()) {
			return;
		}
		Minecraft client = Minecraft.getInstance();
		if (client.options.hideGui || client.player == null) {
			return;
		}
		float alpha = DenialOverlayState.getAlpha();
		if (alpha <= 0f) {
			return;
		}

		Font font = client.font;
		Skill skill = DenialOverlayState.getSkill();
		String levelText = String.valueOf(DenialOverlayState.getLevel());

		int screenWidth = client.getWindow().getGuiScaledWidth();
		int screenHeight = client.getWindow().getGuiScaledHeight();
		int x = (screenWidth - BOX_WIDTH) / 2;
		int y = Math.round(screenHeight * TOP_FRACTION);

		int headerHeight = font.lineHeight;
		int contentHeight = headerHeight + CONTENT_GAP + LevelZIcons.ICON_SIZE;
		int contentTop = y + (BOX_HEIGHT - contentHeight) / 2;
		int headerY = contentTop;
		int iconY = contentTop + headerHeight + CONTENT_GAP;

		PoseStack pose = graphics.pose();

		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1F, 1F, 1F, Math.min(1F, alpha * BG_OPACITY_BOOST));

		pose.pushPose();
		pose.translate(x, y, 0);
		float texScale = BOX_WIDTH / (float) TEX_NATIVE_WIDTH;
		pose.scale(texScale, texScale, 1F);
		graphics.blit(TEXTURE, 0, 0, 0, 0, TEX_NATIVE_WIDTH, TEX_NATIVE_HEIGHT, TEX_NATIVE_WIDTH, TEX_NATIVE_HEIGHT);
		pose.popPose();
		RenderSystem.setShaderColor(1F, 1F, 1F, alpha);

		int headerWidth = font.width(HEADER);
		graphics.drawString(font, HEADER, x + (BOX_WIDTH - headerWidth) / 2, headerY, scaleAlpha(HEADER_COLOR, alpha), true);

		int iconX = x + (BOX_WIDTH - LevelZIcons.ICON_SIZE) / 2;
		graphics.blit(LevelZIcons.TEXTURE, iconX, iconY, LevelZIcons.uFor(skill), LevelZIcons.v(), LevelZIcons.ICON_SIZE, LevelZIcons.ICON_SIZE, LevelZIcons.SHEET_SIZE, LevelZIcons.SHEET_SIZE);

		int levelWidth = font.width(levelText);
		int badgeX = iconX + LevelZIcons.ICON_SIZE - levelWidth;
		int badgeY = iconY + LevelZIcons.ICON_SIZE - font.lineHeight + 1;
		graphics.pose().pushPose();
		graphics.pose().translate(0, 0, 200);
		graphics.drawString(font, levelText, badgeX, badgeY, scaleAlpha(BADGE_COLOR, alpha), true);
		graphics.pose().popPose();

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
	}

	private static int scaleAlpha(int argb, float factor) {
		int a = (argb >>> 24) & 0xFF;
		int scaled = Math.round(a * factor);
		return (scaled << 24) | (argb & 0x00FFFFFF);
	}
}
