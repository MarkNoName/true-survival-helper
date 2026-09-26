package net.truesurvivalhelper;

import net.minecraft.server.packs.resources.IoSupplier;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class TshPackIcon {
	private static final int[] SIZES = {16, 24, 32, 48, 128, 256};
	private static final List<IoSupplier<InputStream>> ICONS = load();

	private TshPackIcon() {
	}

	public static List<IoSupplier<InputStream>> icons() {
		return ICONS;
	}

	private static List<IoSupplier<InputStream>> load() {
		try (InputStream in = TshPackIcon.class.getResourceAsStream("/packicon.png")) {
			if (in == null) {
				return null;
			}
			BufferedImage source = ImageIO.read(in);
			List<IoSupplier<InputStream>> icons = new ArrayList<>();
			for (int size : SIZES) {
				byte[] png = resize(source, size);
				icons.add(() -> new ByteArrayInputStream(png));
			}
			return List.copyOf(icons);
		} catch (IOException e) {
			return null;
		}
	}

	private static byte[] resize(BufferedImage source, int size) throws IOException {
		BufferedImage target;
		if (source.getWidth() == size && source.getHeight() == size) {
			target = source;
		} else {
			target = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = target.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(source, 0, 0, size, size, null);
			g.dispose();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(target, "png", out);
		return out.toByteArray();
	}
}
