package com.etheller.warsmash.viewer5.handlers.blp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.etheller.warsmash.viewer5.GdxTextureResource;
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
import lin.threading.MtPixelTask;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.zip.InflaterInputStream;

public class PixmapTexture extends GdxTextureResource {

	public PixmapTexture(final ModelViewer viewer, final ResourceHandler handler, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(viewer, handler, extension, pathSolver, fetchUrl);
	}

	@Override
	protected void lateLoad() {

	}

	@Override
	protected void load(final InputStream src, final Object options) {
			if (src == null)
				return;
			read(src);
	}

	static private final byte[] readBuffer = new byte[32000];

	void read(InputStream s) {
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
			final Texture texture= new Texture( pixmap);
			pixmap.dispose();
			setGdxTexture(texture);

		}
		catch (Exception e) {
			throw new GdxRuntimeException("Couldn't read Pixmap from inputStream", e);
		} finally {
			StreamUtils.closeQuietly(in);
		}
	}
}