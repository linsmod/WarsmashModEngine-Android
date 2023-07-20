package com.etheller.warsmash.util;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.viewer5.handlers.ResourceInfo;
import com.etheller.warsmash.viewer5.handlers.tga.TgaFile;
import com.google.code.appengine.awt.Transparency;
import com.google.code.appengine.awt.color.ColorSpace;
import com.google.code.appengine.awt.image.BufferedImage;
import com.google.code.appengine.awt.image.ColorModel;
import com.google.code.appengine.awt.image.ComponentColorModel;
import com.google.code.appengine.awt.image.DataBuffer;
import com.google.code.appengine.imageio.ImageIO;
import com.google.code.appengine.imageio.spi.IIORegistry;
import com.google.code.appengine.imageio.spi.ImageReaderSpi;
import com.hiveworkshop.blizzard.blp.BLPReadParam;
import com.hiveworkshop.blizzard.blp.BLPReader;
import com.hiveworkshop.blizzard.blp.BLPReaderSpi;
import com.lin.imageio.plugins.jpeg.JPEGImageReaderSpi;
import lin.threading.MtPixelTask;
import lin.threading.DecodedBitmap;
import net.hydromatic.linq4j.Linq;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;

/**
 * Uses AWT stuff
 */
public final class ImageUtils {
	private static final int BYTES_PER_PIXEL = 4;
	public static final String DEFAULT_ICON_PATH = "ReplaceableTextures\\CommandButtons\\BTNTemp.blp";

	static {
		IIORegistry.getDefaultInstance().registerServiceProvider(new BLPReaderSpi());
		IIORegistry.getDefaultInstance().registerServiceProvider(new JPEGImageReaderSpi());
	}

	public static Texture getAnyExtensionTexture(final DataSource dataSource, final String path) {
		try {
			String[] paths = new String[]{
					path,
					changeExtension(path, ".tga"),
					changeExtension(path, ".dds")
			};
			for (String maybePath :
					paths) {
				if (dataSource.has(maybePath)) {
					var res = new ResourceInfo(dataSource, maybePath);
					return getTexture(res);
				}
			}
			System.err.println("[RES_NOT_FOUND] " + path + " IN ANY EXT");
			var kw = new FileHandle(path).nameWithoutExtension().toLowerCase();
			var files = Linq.of(dataSource.getListfile())
								.where(x -> x.toLowerCase().contains(kw)).toList();
			for (String found :
					files) {
				System.out.println("maybe file: " + found);
			}
		}
		catch (final IOException e) {
			return null;
		}
		return null;
	}

	public static String changeExtension(String f, String newExtension) {
		int i = f.lastIndexOf('.');
		String name = f.substring(0, i);
		String path = new File(name + newExtension).getPath();
//		System.out.println("[changeExtension] input=" + f + " output=" + path);
		return path;
	}

	public static AnyExtensionImage getAnyExtensionImageFixRGB(final DataSource dataSource, final String path,
			final String errorType) throws IOException {
		if (path.toLowerCase().endsWith(".blp")) {
			try (InputStream stream = dataSource.getResourceAsStream(path)) {
				if (stream == null) {
					final String tgaPath = path.substring(0, path.length() - 4) + ".tga";
					try (final InputStream tgaStream = dataSource.getResourceAsStream(tgaPath)) {
						if (tgaStream != null) {
							final BufferedImage tgaData = TgaFile.readTGA(tgaPath, tgaStream);
							return new AnyExtensionImage(false, tgaData);
						}
						else {
							final String ddsPath = path.substring(0, path.length() - 4) + ".dds";
							try (final InputStream ddsStream = dataSource.getResourceAsStream(ddsPath)) {
								if (ddsStream != null) {
									final BufferedImage image = ImageIO.read(ddsStream);
									return new AnyExtensionImage(false, image);
								}
								else {
									throw new IllegalStateException("Missing " + errorType + ": " + path);
								}
							}
						}
					}
				}
				else {
					final BufferedImage image = ImageIO.read(stream);
					return new AnyExtensionImage(true, image);
				}
			}
		}
		else {
			throw new IllegalStateException("Missing " + errorType + ": " + path);
		}
	}

	public static final class AnyExtensionImage {
		private final boolean needsSRGBFix;
		private final BufferedImage imageData;

		public AnyExtensionImage(final boolean needsSRGBFix, final BufferedImage imageData) {
			this.needsSRGBFix = needsSRGBFix;
			this.imageData = imageData;
		}

		public BufferedImage getImageData() {
			return this.imageData;
		}

		public BufferedImage getRGBCorrectImageData() {
			return this.needsSRGBFix ? forceBufferedImagesRGB(this.imageData) : this.imageData;
		}

		public boolean isNeedsSRGBFix() {
			return this.needsSRGBFix;
		}
	}

	public static Texture getTexture(final BufferedImage image, final boolean sRGBFix) {
		final int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		// 4
		// for
		// RGBA,
		// 3
		// for
		// RGB

		final Pixmap pixmap = sRGBFix ? new Pixmap(image.getWidth(), image.getHeight(), Format.RGBA8888) {
			@Override
			public int getGLInternalFormat() {
				return GL30.GL_SRGB8_ALPHA8;
			}
		} : new Pixmap(image.getWidth(), image.getHeight(), Format.RGBA8888);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				final int pixel = pixels[(y * image.getWidth()) + x];
				pixmap.drawPixel(x, y, (pixel << 8) | (pixel >>> 24));
			}
		}
		final Texture texture = new Texture(pixmap);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return texture;
	}

	public static DecodedBitmap decodeBLP(ResourceInfo info) throws IOException {
		return decodeBLP(info.getResourceAsStream());
	}

	public static DecodedBitmap decodeBLP(InputStream stream) throws IOException {
		BLPReader reader = new BLPReader(null);
		reader.setInput(new ByteSourceInputStream(stream, null));
		var image = reader.read(0, new BLPReadParam() {
			@Override
			public boolean isDirectRead() {
				return true;
			}

			@Override
			public ImageReaderSpi getJPEGSpi() {
				return new JPEGImageReaderSpi();
			}
		});

		//speedup read using multi-threading task
		int batchSize = 64;
		var task = MtPixelTask.create(image::getRGB, image.getWidth(), image.getHeight(), batchSize);
		return task.read();
	}

	public static DecodedBitmap decodeRes(ResourceInfo info) throws IOException {
		var file = info.getCacheFile("blp2png", ".png");
//		var temp = info.getCacheFile("blp2png", ".png.tmp");
//		if (temp.exists())
//			temp.delete();
		if (!file.exists()) {
			file.parent().mkdirs();
			ByteArrayOutputStream bos = new ByteArrayOutputStream(100 << 10);

			var fs = new FileOutputStream(file.file()) {
				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					super.write(b, off, len);
					bos.write(b, off, len);
				}

				@Override
				public void flush() throws IOException {
					bos.flush();
					super.flush();
				}
			};

			var image = com.google.code.appengine.imageio.ImageIO.read(info.getResourceAsStream());
			com.google.code.appengine.imageio.ImageIO.write(image, "png", fs);

//			temp.moveTo(file);
//			temp.delete();
			System.out.println("[WRITE_BLP_PNG] " + file.path());
			return rgbaEncode(image);
		}
		else {

			// load converted png from cache for the blp.
//			System.out.println("[LOAD_BLP_PNG] " + file.path());

			var image = com.google.code.appengine.imageio.ImageIO.read(file.file());
			return rgbaEncode(image);
		}
	}

	static DecodedBitmap rgbaEncode(BufferedImage image) {
		int batchSize = 64;
		var task = MtPixelTask.create(image::getRGB, image.getWidth(), image.getHeight(), batchSize);
		return task.read();
	}

	/**
	 * @param res blp/jpeg/png/dds/tga should be supported by awt-imageio
	 * @return
	 * @throws IOException
	 */
	public static Texture getTexture(ResourceInfo res) throws IOException {
		Pixmap pixmap = getPixmap(res);
		final Texture texture = new Texture(pixmap);
		pixmap.dispose();
		return texture;
	}

	public static AbstractBitmap getBitmap(ResourceInfo res) throws IOException {
		Pixmap pixmap = getPixmap(res);
		return new AbstractBitmap() {
			ByteBuffer buffer;

			@Override
			public int getHeight() {
				return pixmap.getWidth();
			}

			@Override
			public int getWidth() {
				return pixmap.getHeight();
			}

			@Override
			public ByteBuffer getBuffer() {
				if (buffer == null) {
//					buffer = BufferUtils.createByteBuffer(pixmap.getWidth() * pixmap.getHeight() * 4);
					buffer = ByteBuffer.allocateDirect(pixmap.getWidth() * pixmap.getHeight() * 4)
									 .order(ByteOrder.nativeOrder());
					for (int y = 0; y < pixmap.getHeight(); y++) {
						for (int x = 0; x < pixmap.getWidth(); x++) {
							int pixel = pixmap.getPixel(x, y);
							pixel = (pixel >>> 8) | (pixel << (32 - 8));
//							buffer.putInt((pixel >>> 8) | (pixel << (32 - 8)));
							buffer.put((byte) ((pixel >> 16) & 0xFF));
							buffer.put((byte) ((pixel >> 8) & 0xFF));
							buffer.put((byte) ((pixel >> 0) & 0xFF));
							buffer.put((byte) ((pixel >> 24) & 0xFF));
						}
					}
					buffer.flip();
				}
				return buffer;
			}
		};
	}

	public static Pixmap getPixmap(ResourceInfo res) throws IOException {
		final ResourceInfo info = res;
		var file = info.getCacheFile("blp2png", ".png");
//		var temp = info.getCacheFile("blp2png", ".png.tmp");
//		if (temp.exists())
//			temp.delete();
		if (!file.exists()) {
			file.parent().mkdirs();
			ByteArrayOutputStream bos = new ByteArrayOutputStream(100 << 10);

			var fs = new FileOutputStream(file.file()) {
				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					super.write(b, off, len);
					bos.write(b, off, len);
				}

				@Override
				public void flush() throws IOException {
					bos.flush();
					super.flush();
				}
			};

			var image = com.google.code.appengine.imageio.ImageIO.read(res.getResourceAsStream());
			com.google.code.appengine.imageio.ImageIO.write(image, "png", fs);

//			temp.moveTo(file);

			var pngData = bos.toByteArray();
			Pixmap pixmap = new Pixmap(pngData, 0, pngData.length);
			System.out.println("[WRITE_BLP_PNG] " + file.path());
			return pixmap;
		}
		else {

			// load converted png from cache for the blp.
//			System.out.println("[LOAD_BLP_PNG] " + file.path());
			Pixmap pixmap = new Pixmap(file);
			try {
				var size = Imaging.getImageSize(info.getResourceAsStream(), info.path);
				if (size.getHeight() != pixmap.getHeight() || size.getWidth() != pixmap.getWidth()) {
					System.err.println("png cache broken.");
				}
			}
			catch (ImageReadException e) {
				throw new RuntimeException(e);
			}
			return pixmap;
		}
	}

	public static Texture getTextureNoColorCorrection(final BufferedImage image) {
		final int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		// 4
		// for
		// RGBA,
		// 3
		// for
		// RGB

		final Pixmap pixmap = new Pixmap(image.getWidth(), image.getHeight(), Format.RGBA8888);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				final int pixel = pixels[(y * image.getWidth()) + x];
				pixmap.drawPixel(x, y, (pixel << 8) | (pixel >>> 24));
			}
		}
		final Texture texture = new Texture(pixmap);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return texture;
	}

	public static Buffer getTextureBuffer(final BufferedImage image) {

		final int imageWidth = image.getWidth();
		final int imageHeight = image.getHeight();
		final int[] pixels = new int[imageWidth * imageHeight];
		image.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

		final ByteBuffer buffer = ByteBuffer.allocateDirect(imageWidth * imageHeight * BYTES_PER_PIXEL)
										  .order(ByteOrder.nativeOrder());
		// 4
		// for
		// RGBA,
		// 3
		// for
		// RGB

		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {
				final int pixel = pixels[(y * imageWidth) + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component.
				// Only for RGBA
			}
		}

		buffer.flip();
		return buffer;
	}

	/**
	 * Convert an input buffered image into sRGB color space using component values
	 * directly instead of performing a color space conversion.
	 *
	 * @param in Input image to be converted.
	 * @return Resulting sRGB image.
	 */
	public static BufferedImage forceBufferedImagesRGB(final BufferedImage in) {
		// Resolve input ColorSpace.
		final ColorSpace inCS = in.getColorModel().getColorSpace();
		final ColorSpace sRGBCS = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		if (inCS == sRGBCS) {
			// Already is sRGB.
			return in;
		}
		if (inCS.getNumComponents() != sRGBCS.getNumComponents()) {
			throw new IllegalArgumentException("Input color space has different number of components from sRGB.");
		}

		// Draw input.
		final ColorModel lRGBModel = new ComponentColorModel(inCS, true, false, Transparency.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);
		final ColorModel sRGBModel = new ComponentColorModel(sRGBCS, true, false, Transparency.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);
		final BufferedImage lRGB = new BufferedImage(lRGBModel,
				lRGBModel.createCompatibleWritableRaster(in.getWidth(), in.getHeight()), false, null);
		for (int i = 0; i < in.getWidth(); i++) {
			for (int j = 0; j < in.getHeight(); j++) {
				lRGB.setRGB(i, j, in.getRGB(i, j));
			}
		}

		// Convert to sRGB.
		final BufferedImage sRGB = new BufferedImage(sRGBModel, lRGB.getRaster(), false, null);

		return sRGB;
	}

	private ImageUtils() {
	}
}
