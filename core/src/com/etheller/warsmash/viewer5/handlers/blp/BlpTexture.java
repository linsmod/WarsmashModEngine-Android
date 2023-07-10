package com.etheller.warsmash.viewer5.handlers.blp;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.RawOpenGLTextureResource;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;
import com.google.code.appengine.imageio.spi.IIORegistry;
import com.google.code.appengine.imageio.spi.ImageReaderSpi;
import com.hiveworkshop.blizzard.blp.BLPReadParam;
import com.hiveworkshop.blizzard.blp.BLPReader;
import com.hiveworkshop.blizzard.blpAwt.BLPReaderSpi;
import com.lin.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.lin.imageio.plugins.jpeg.JPEGImageWriterSpi;
import lin.threading.MtPixelTask;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;

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
			if (src == null)
				return;
//			update(ImageIO.read(src), true);

//			update(com.google.code.appengine.imageio.ImageIO.read(src), true);
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

			//sync mode
			super.update(task.read().getBuffer(), image.getWidth(), image.getHeight(), sRGBFix);

			//async mode
			//no errors, but many textures is black.
//			task.readAsync(x->{
//				super.update(x.getBuffer(), image.getWidth(), image.getHeight(), sRGBFix);
//			});
		}
		else {
			update(image, sRGBFix);
		}
	}
}