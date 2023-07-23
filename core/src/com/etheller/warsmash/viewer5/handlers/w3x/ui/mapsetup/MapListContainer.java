package com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup;

import java.io.File;
import java.util.*;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame.ListBoxSelectionListener;
import com.etheller.warsmash.util.AbstractListItemDisplay;
import com.etheller.warsmash.util.AbstractListItemProperty;
import com.etheller.warsmash.util.ListItemEnum;
import com.google.common.base.Strings;
import net.hydromatic.linq4j.Linq;

import static com.etheller.warsmash.datasources.FolderDataSource.fixFilepath;

public class MapListContainer {
	private final SimpleFrame mapListContainer;
	private final ListBoxFrame mapListBox;
	private final Collection<String> listfile;
	private final GameUI rootFrame;
	private final Viewport uiViewport;

	public MapListContainer(final GameUI rootFrame, final Viewport uiViewport, final String containerKey,
			final DataSource dataSource, final BitmapFont font) {
		this.rootFrame = rootFrame;
		this.uiViewport = uiViewport;
		this.mapListContainer = (SimpleFrame) rootFrame.getFrameByName(containerKey, 0);
		this.mapListBox = (ListBoxFrame) rootFrame.createFrameByType("LISTBOX", "MapListBox", this.mapListContainer,
				"WITHCHILDREN", 0);
		this.mapListBox.setSetAllPoints(true);
		this.mapListBox.setFrameFont(font);
		this.listfile = Linq.of(dataSource.getListfile())
								.where(x -> x.endsWith(".w3x") || x.endsWith(".w3m"))
								.toList();
		this.listFolder(new Stack<>());
		this.mapListContainer.add(this.mapListBox);
	}

	public void addSelectionListener(final ListBoxSelectionListener listener) {
		this.mapListBox.setSelectionListener(listener);
	}

	public AbstractListItemProperty getSelectedItem() {
		return this.mapListBox.getSelectedItem();
	}

	HashMap<String, List<AbstractListItemProperty>> cached = new HashMap<>();

	public void listFolder(Stack<String> folderPaths) {
		this.mapListBox.removeAllItems();
		var path = String.join(File.separator, folderPaths.stream().toArray(String[]::new));
		if (cached.containsKey(path)) {
			this.mapListBox.setItems(cached.get(path), rootFrame, uiViewport);

		}
		else {
			final List<String> displayItemPaths = new ArrayList<>();
			final List<String> displayItemFolders = new ArrayList<>();
			var isRoot = Strings.isNullOrEmpty(path);
			for (final String file : listfile) {
				//ignore non map files
				if (!file.endsWith(".w3x") && !file.endsWith(".w3m")) {
					continue;
				}
				//ignore files do not in the path;
				if (!Strings.isNullOrEmpty(path) && !file.startsWith(path)) {
					continue;
				}
				var relative = relative(file, path);
//				System.out.println("[LIST_FOLDER] " + path + " -> " + relative);
				//ignore folder itself
				if (Strings.isNullOrEmpty(relative))
					continue;

				int idx = relative.indexOf("/") == -1 ? relative.indexOf("\\") : -1;


				if (idx == -1) {
					//filename only
					displayItemPaths.add(relative);
				}
				else {
					//file is under a folder
					var folder = relative.substring(0, idx);
					//ignore as already list files under Maps folder above directly
					//Maps folder is in mpq archives.
//				if (isRoot && file.equals("Maps")) {
//					continue;
//				}
					if (!displayItemFolders.contains(folder))
						displayItemFolders.add(folder);
				}
			}
			//folders firstly
			for (final String displayItemPath : displayItemFolders) {
				this.mapListBox.addItem(displayItemPath, ListItemEnum.ITEM_FOLDER, rootFrame, uiViewport);
			}
			//files secondly
			for (final String displayItemPath : displayItemPaths) {
				this.mapListBox.addItem(absolute(displayItemPath, path), ListItemEnum.ITEM_MAP, rootFrame, uiViewport);
			}
			this.mapListBox.sortItems();

			// GoToParent on the top
			if (!Strings.isNullOrEmpty(path)) {
				this.mapListBox.addItem(0, "..", ListItemEnum.ITEM_FOLDER, rootFrame, uiViewport);
			}

			this.cached.put(path, this.mapListBox.getListItems().stream().toList());
		}
		System.out.println("[LIST_MAP_DONE]");

		//rendered view is not updating after mapListBox items changed
		//mouse move can trigger updates too.
		//we do a force refresh here.
		rootFrame.positionBounds(rootFrame, uiViewport);
	}

	String absolute(String path, String folder) {
		if (Strings.isNullOrEmpty(folder))
			return path;
		return folder + (path.startsWith(File.separator) ? path : (File.separator + path));
	}

	String relative(String path, String folder) {
		path = fixFilepath(path);
		folder = fixFilepath(folder);

		if (path.startsWith(folder)) {
			path = path.substring(folder.length());
		}
		if (path.startsWith(File.separator)) {
			path = path.substring(1);
		}
		return path;
	}
}
