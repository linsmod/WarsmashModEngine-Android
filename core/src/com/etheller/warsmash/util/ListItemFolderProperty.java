package com.etheller.warsmash.util;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;

public class ListItemFolderProperty extends AbstractListItemProperty {

	public final String displayName;
	private boolean isGoToParent;

	public ListItemFolderProperty(ListItemEnum dataType, String rawValue, GameUI gameUI, DataSource data) {
		super(dataType, rawValue);
		displayName = rawValue;
	}

	@Override
	public int compare(AbstractListItemProperty itemProperty) {
		if (this.getItemType() != itemProperty.getItemType()) {
			return 1;
		}
		ListItemFolderProperty another = (ListItemFolderProperty) itemProperty;
		return this.displayName.compareTo(another.displayName);
	}

	public boolean isGoToParent() {
		return isGoToParent;
	}

	public void setGoToParent(boolean goToParent) {
		isGoToParent = goToParent;
	}
}
