package com.etheller.warsmash.viewer5.handlers.blp;

import java.io.*;
import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.viewer5.GdxTextureResource;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;
import com.etheller.warsmash.viewer5.handlers.ResourceInfo;
import com.google.code.appengine.imageio.ImageIO;
import com.google.code.appengine.imageio.spi.IIORegistry;
import com.google.code.appengine.imageio.spi.ImageReaderSpi;
import com.google.code.appengine.imageio.stream.ImageOutputStream;
import com.google.code.appengine.imageio.stream.MemoryCacheImageOutputStream;
import com.hiveworkshop.blizzard.blp.BLPReadParam;
import com.hiveworkshop.blizzard.blp.BLPReader;
import com.hiveworkshop.blizzard.blpAwt.BLPReaderSpi;
import com.lin.imageio.plugins.jpeg.JPEGImageReaderSpi;
import lin.threading.MtPixelTask;
import lin.threading.RgbaImageBuffer;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;

import javax.imageio.stream.FileCacheImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class BlpGdxTexture extends GdxTextureResource {

	public BlpGdxTexture(final ModelViewer viewer, final ResourceHandler handler, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(viewer, handler, extension, pathSolver, fetchUrl);
	}

	@Override
	protected void lateLoad() {

	}

	@Override
	protected void load(final InputStream src, final Object options) {
		ResourceInfo info = (ResourceInfo) options;
		try {
			Pixmap pixmap = ImageUtils.getPixmap(info);
			final Texture texture = new Texture(pixmap);
			setFilter(texture);
			setGdxTexture(texture);
//			String path = info.getCachePath("blp2png", ".png");
//			var file = Gdx.files.external(path);
//			if (!file.exists()) {
//				file.parent().mkdirs();
//
//				ByteArrayOutputStream bos = new ByteArrayOutputStream(100 << 10);
//				var fs= new FileOutputStream( file.file()){
//					@Override
//					public void write(byte[] b, int off, int len) throws IOException {
//						super.write(b, off, len);
//						bos.write(b,off,len);
//					}
//
//					@Override
//					public void flush() throws IOException {
//						bos.flush();
//						super.flush();
//					}
//				};
//				var rendered = ImageIO.read(src);
//				ImageIO.write(rendered, "png", fs);
//
//				var pngData = bos.toByteArray();
//				Pixmap pixmap = new Pixmap(pngData,0,pngData.length);
//				final Texture texture = new Texture(pixmap);
//				setFilter(texture);
//				setGdxTexture(texture);
//				pixmap.dispose();
//
//				System.out.println("[WRITE_BLP_PNG] " + file.path());
//			}
//			else {
//				// load converted png from cache for the blp.
//			System.out.println("[LOAD_BLP_PNG] " + file.path());
//				Pixmap pixmap = new Pixmap(file);
//				final Texture texture = new Texture(pixmap);
//				setFilter(texture);
//				setGdxTexture(texture);
//				pixmap.dispose();
//			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	void setFilter(Texture texture) {
//		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
	}

	@Override
	protected void error(Exception e) {
		throw new RuntimeException(e);
	}
}
