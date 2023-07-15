package com.etheller.warsmash.desktop.editor.w3m.util;

import com.google.code.appengine.awt.image.BufferedImage;
import java.io.IOException;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.util.ImageUtils;

public class WorldEditArt {
	public static final String UNTITLED_DOODAD_ICON_PATH = "ReplaceableTextures\\WorldEditUI\\DoodadPlaceholder.blp";

	private final DataTable worldEditorData;
	private final DataSource gameDataSource;

	public WorldEditArt(final DataSource gameDataSource, final DataTable worldEditorData) {
		this.gameDataSource = gameDataSource;
		this.worldEditorData = worldEditorData;
	}
}
