package com.etheller.warsmash.viewer5.handlers.blp;

import java.util.ArrayList;

import com.etheller.warsmash.viewer5.HandlerResource;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;
import com.etheller.warsmash.viewer5.handlers.ResourceHandlerConstructionParams;

public class BlpHandler extends ResourceHandler {

	public BlpHandler() {
		this.extensions = new ArrayList<>();
		this.extensions.add(new String[] { ".blp", "arrayBuffer" });
	}

	@Override
	public boolean load(final ModelViewer modelViewer) {
		return true;
	}

	@Override
	public HandlerResource<?> construct(final ResourceHandlerConstructionParams params) {
		return new BlpGdxTexture(params.getViewer(),params.getHandler(),params.getExtension(),params.getPathSolver(),params.getFetchUrl());
//		return new BlpTexture(params.getViewer(), params.getHandler(), params.getExtension(), params.getPathSolver(),
//				params.getFetchUrl());
	}

}
