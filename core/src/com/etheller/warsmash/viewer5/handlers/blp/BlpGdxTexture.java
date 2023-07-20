package com.etheller.warsmash.viewer5.handlers.blp;

import java.io.*;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.viewer5.GdxTextureResource;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;
import com.etheller.warsmash.viewer5.handlers.ResourceInfo;

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
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	void setFilter(Texture texture) {
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
	}

	@Override
	protected void error(Exception e) {
		throw new RuntimeException(e);
	}
}
