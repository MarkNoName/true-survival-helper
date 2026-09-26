package net.truesurvivalhelper;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TshItemModelSides {
	private static final float MIN_Z = 7.5F;
	private static final float MAX_Z = 8.5F;
	private static final float UV_SHRINK = 0.1F;
	private static final Side[] SIDES = Side.values();

	private TshItemModelSides() {
	}

	public static List<BlockElement> create(SpriteContents contents, String texture, int tintIndex) {
		float scaleX = 16.0F / contents.width();
		float scaleY = 16.0F / contents.height();
		List<BlockElement> elements = new ArrayList<>();

		for (SideFace face : collect(contents)) {
			Side side = face.side();
			float x = face.x();
			float y = face.y();
			float u0 = x + UV_SHRINK;
			float u1 = x + 1.0F - UV_SHRINK;
			float v0;
			float v1;
			if (side.isHorizontal()) {
				v0 = y + UV_SHRINK;
				v1 = y + 1.0F - UV_SHRINK;
			} else {
				v0 = y + 1.0F - UV_SHRINK;
				v1 = y + UV_SHRINK;
			}

			float x0 = x;
			float y0 = y;
			float x1 = x;
			float y1 = y;
			switch (side) {
				case UP -> x1++;
				case DOWN -> {
					x1++;
					y0++;
					y1++;
				}
				case LEFT -> y1++;
				case RIGHT -> {
					x0++;
					x1++;
					y1++;
				}
			}

			x0 *= scaleX;
			x1 *= scaleX;
			y0 = 16.0F - y0 * scaleY;
			y1 = 16.0F - y1 * scaleY;
			BlockFaceUV uv = new BlockFaceUV(new float[]{u0 * scaleX, v0 * scaleY, u1 * scaleX, v1 * scaleY}, 0);
			Map<Direction, BlockElementFace> faces = Map.of(side.direction, new BlockElementFace(null, tintIndex, texture, uv));
			switch (side) {
				case UP -> elements.add(element(x0, y0, x1, y0, faces));
				case DOWN -> elements.add(element(x0, y1, x1, y1, faces));
				case LEFT -> elements.add(element(x0, y0, x0, y1, faces));
				case RIGHT -> elements.add(element(x1, y0, x1, y1, faces));
			}
		}

		return elements;
	}

	private static BlockElement element(float fromX, float fromY, float toX, float toY, Map<Direction, BlockElementFace> faces) {
		return new BlockElement(new Vector3f(fromX, fromY, MIN_Z), new Vector3f(toX, toY, MAX_Z), faces, null, true);
	}

	private static Set<SideFace> collect(SpriteContents contents) {
		int width = contents.width();
		int height = contents.height();
		Set<SideFace> faces = new LinkedHashSet<>();
		contents.getUniqueFrames().forEach(frame -> {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (isTransparent(contents, frame, x, y, width, height)) {
						continue;
					}

					for (Side side : SIDES) {
						if (isTransparent(contents, frame, x - side.direction.getStepX(), y - side.direction.getStepY(), width, height)) {
							faces.add(new SideFace(side, x, y));
						}
					}
				}
			}
		});
		return faces;
	}

	private static boolean isTransparent(SpriteContents contents, int frame, int x, int y, int width, int height) {
		return x < 0 || y < 0 || x >= width || y >= height || contents.isTransparent(frame, x, y);
	}

	private enum Side {
		UP(Direction.UP),
		DOWN(Direction.DOWN),
		LEFT(Direction.EAST),
		RIGHT(Direction.WEST);

		private final Direction direction;

		Side(Direction direction) {
			this.direction = direction;
		}

		private boolean isHorizontal() {
			return this == UP || this == DOWN;
		}
	}

	private record SideFace(Side side, int x, int y) {
	}
}
