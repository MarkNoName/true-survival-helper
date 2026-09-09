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

/**
 * Draws the "requirement not met" box in the upper third of the screen while
 * {@link DenialOverlayState} has an active message: a red header line, and below it
 * LevelZ's own icon for the skill with the required level as a small badge on its
 * bottom-right corner (matching vanilla's item stack count style).
 *
 * The soft-edged background is a pre-rendered texture (assets/tsh/textures/gui/
 * denial_box.png, the user's own image) rather than drawn with GuiGraphics primitives
 * - fill/fillGradient can only do flat fills and one-axis (top-to-bottom) gradients,
 * there's no way to get a real blurred/rounded edge out of them - and blitted here
 * scaled down (via a pose-stack scale, sampling the full-resolution source rather than
 * pre-shrinking the file) from its native size to the on-screen box size.
 */
@Environment(EnvType.CLIENT)
public final class DenialOverlayRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tsh", "textures/gui/denial_box.png");
	private static final int TEX_NATIVE_WIDTH = 809;
	private static final int TEX_NATIVE_HEIGHT = 194;
	private static final int BOX_WIDTH = 250;
	private static final int BOX_HEIGHT = Math.round(BOX_WIDTH * (float) TEX_NATIVE_HEIGHT / TEX_NATIVE_WIDTH);
	/** Extra opacity multiplier on top of the source PNG's own alpha - it still reads as too faint on its own. */
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

		// Center the header+icon block vertically within the box.
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

		// Level number as a badge on the icon's bottom-right corner, vanilla item
		// stack count style, instead of separate text beside the icon.
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
