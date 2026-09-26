package net.levelz.util;

import java.util.Arrays;
import java.util.List;

import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class TooltipUtil {
	public static void renderTooltip(Minecraft client, GuiGraphics context) {
		if (client.hitResult != null && ConfigInit.CONFIG.showLockedBlockInfo) {
			HitResult hitResult = client.hitResult;
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				Block block = client.level.getBlockState(((BlockHitResult) hitResult).getBlockPos()).getBlock();
				int blockId = BuiltInRegistries.BLOCK.getId(block);
				if (PlayerStatsManager.listContainsItemOrBlock(client.player, blockId, 1)) {
					renderTooltip(client, context, Arrays.asList(Component.nullToEmpty(block.getName().getString()), Component.nullToEmpty("Mineable Lv. " + PlayerStatsManager.getUnlockLevel(blockId, 1))),
							BuiltInRegistries.BLOCK.getKey(block), context.guiWidth() / 2 + ConfigInit.CONFIG.lockedBlockInfoPosX, ConfigInit.CONFIG.lockedBlockInfoPosY);
				}
			}
		}
	}

	private static void renderTooltip(Minecraft client, GuiGraphics context, List<Component> text, ResourceLocation identifier, int x, int y) {
		int textWidth = client.font.width(text.get(0)) > client.font.width(text.get(1)) ? client.font.width(text.get(0)) : client.font.width(text.get(1));
		int l = x - textWidth / 2 - 3;
		int m = y + 4;
		int k = textWidth + 23;
		int n = 17;

		context.pose().pushPose();

		int colorStart = 0xBF191919;
		int colorTwo = 0xBF7F0200;
		int colorThree = 0xBF380000;

		render(context, l, m, k, n, 400, colorStart, colorTwo, colorThree);

		context.pose().translate(0.0, 0.0, 400.0);

		context.drawString(client.font, text.get(0), x - k / 2 + 30, y + 4, 0xFFFFFF, false);
		context.drawString(client.font, text.get(1), x - k / 2 + 30, y + 14, 0xFFFFFF, false);

		context.renderItem(BuiltInRegistries.ITEM.get(identifier).getDefaultInstance(), x - k / 2 + 11, y + 5);
		context.pose().popPose();
	}

	public static void render(GuiGraphics context, int x, int y, int width, int height, int z, int background, int borderColorStart, int borderColorEnd) {
		int i = x - 3;
		int j = y - 3;
		int k = width + 3 + 3;
		int l = height + 3 + 3;
		renderHorizontalLine(context, i, j - 1, k, z, background);
		renderHorizontalLine(context, i, j + l, k, z, background);
		renderRectangle(context, i, j, k, l, z, background);
		renderVerticalLine(context, i - 1, j, l, z, background);
		renderVerticalLine(context, i + k, j, l, z, background);
		renderBorder(context, i, j + 1, k, l, z, borderColorStart, borderColorEnd);
	}

	private static void renderBorder(GuiGraphics context, int x, int y, int width, int height, int z, int startColor, int endColor) {
		renderVerticalLine(context, x, y, height - 2, z, startColor, endColor);
		renderVerticalLine(context, x + width - 1, y, height - 2, z, startColor, endColor);
		renderHorizontalLine(context, x, y - 1, width, z, startColor);
		renderHorizontalLine(context, x, y - 1 + height - 1, width, z, endColor);
	}

	private static void renderVerticalLine(GuiGraphics context, int x, int y, int height, int z, int color) {
		context.fill(x, y, x + 1, y + height, z, color);
	}

	private static void renderVerticalLine(GuiGraphics context, int x, int y, int height, int z, int startColor, int endColor) {
		context.fillGradient(x, y, x + 1, y + height, z, startColor, endColor);
	}

	private static void renderHorizontalLine(GuiGraphics context, int x, int y, int width, int z, int color) {
		context.fill(x, y, x + width, y + 1, z, color);
	}

	private static void renderRectangle(GuiGraphics context, int x, int y, int width, int height, int z, int color) {
		context.fill(x, y, x + width, y + height, z, color);
	}
}
