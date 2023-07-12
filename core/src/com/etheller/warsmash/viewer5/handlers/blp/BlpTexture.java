package com.etheller.warsmash.viewer5.handlers.blp;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.zip.InflaterInputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.RawOpenGLTextureResource;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;
import com.google.code.appengine.awt.color.ColorSpace;
import com.google.code.appengine.imageio.spi.IIORegistry;
import com.google.code.appengine.imageio.spi.ImageReaderSpi;
import com.hiveworkshop.blizzard.blp.BLPReadParam;
import com.hiveworkshop.blizzard.blp.BLPReader;
import com.hiveworkshop.blizzard.blpAwt.BLPReaderSpi;
import com.lin.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.lin.imageio.plugins.jpeg.JPEGImageWriterSpi;
import lin.threading.MtPixelTask;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;
import org.apache.harmony.awt.gl.color.LUTColorConverter;

public class BlpTexture extends RawOpenGLTextureResource {

	public BlpTexture(final ModelViewer viewer, final ResourceHandler handler, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(viewer, extension, pathSolver, fetchUrl, handler);
	}

	@Override
	protected void lateLoad() {

	}

	@Override
	protected void load(final InputStream src, final Object options) {
		try {
//			if (src == null)
//				return;
//			update(BlpFactory.createBlp(src), false);
//			update(ImageIO.read(src), true);

//			update(com.google.code.appengine.imageio.ImageIO.read(src), true);
//			LUTColorConverter.sRGB_CS = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
//			LUTColorConverter.LINEAR_RGB_CS = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
			update(src, true);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	static {
		javax.imageio.spi.IIORegistry.getDefaultInstance().registerServiceProvider(new BLPReaderSpi());
		IIORegistry.getDefaultInstance().registerServiceProvider(new JPEGImageReaderSpi());
	}

	public void update(final InputStream inputStream, final boolean sRGBFix) throws IOException {
		BLPReader reader = new BLPReader(null);
		reader.setInput(new ByteSourceInputStream(inputStream, null));
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
		if (true) {
			updateSize(image.getWidth(), image.getHeight());


			int batchSize = 64;
			var task = MtPixelTask.create(image::getRGB, image.getWidth(), image.getHeight(), batchSize);

			//pixmap mode
			Pixmap pixmap = new Pixmap(image.getWidth(), image.getHeight(), Pixmap.Format.RGBA8888);
			ByteBuffer pixelBuf = pixmap.getPixels();
			((Buffer) pixelBuf).position(0);
			((Buffer) pixelBuf).limit(pixelBuf.capacity());
			task.read(pixelBuf);
			//sync mode
//			PixmapIO.writePNG(Gdx.files.external("blp2png/"), pixmap);
			new Texture(pixmap);

			super.update(pixelBuf, image.getWidth(), image.getHeight(), sRGBFix);
			pixmap.dispose();
		}
		else {
			update(image, sRGBFix);
		}
	}
	private void fixColors(BufferedImage img) {
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int rgb = img.getRGB(x, y);
				java.awt.Color color1 = new java.awt.Color(rgb);
				java.awt.Color color2 = new Color(color1.getBlue(), color1.getGreen(), color1.getRed());
				img.setRGB(x, y, color2.hashCode());
			}
		}
	}

	static private final byte[] readBuffer = new byte[32000];

	static public Pixmap read(InputStream s) {
		DataInputStream in = null;

		try {
			in = new DataInputStream(new InflaterInputStream(new BufferedInputStream(s)));
			int width = in.readInt();
			int height = in.readInt();
			Pixmap.Format format = Pixmap.Format.fromGdx2DPixmapFormat(in.readInt());
			Pixmap pixmap = new Pixmap(width, height, format);
			ByteBuffer pixelBuf = pixmap.getPixels();
			((Buffer) pixelBuf).position(0);
			((Buffer) pixelBuf).limit(pixelBuf.capacity());

			synchronized (readBuffer) {
				int readBytes = 0;
				while ((readBytes = in.read(readBuffer)) > 0) {
					pixelBuf.put(readBuffer, 0, readBytes);
				}
			}

			((Buffer) pixelBuf).position(0);
			((Buffer) pixelBuf).limit(pixelBuf.capacity());
			return pixmap;
		}
		catch (Exception e) {
			throw new GdxRuntimeException("Couldn't read Pixmap from inputStream", e);
		} finally {
			StreamUtils.closeQuietly(in);
		}
	}
}