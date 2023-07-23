package com.etheller.warsmash.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.frames.*;

public class ListItemFolderDisplay extends AbstractListItemDisplay {

	private final BackdropFrame mapBackdrop;
	private final SingleStringFrame displayText;

	private final GameUI gameUI;

	public ListItemFolderDisplay(ListItemEnum dataType, String displayText, ListBoxFrame rootList, GameUI gameUI, Viewport viewport) {
		super(dataType, displayText, rootList, gameUI, viewport);
		final float mapIconSize = (float) Math.floor(GameUI.convertY(viewport, 0.018f));
		final BitmapFont refFont = ((StringFrame) gameUI.getFrameByName("MaxPlayersValue", 0)).getFrameFont();
		this.gameUI = gameUI;

		mapBackdrop = (BackdropFrame) gameUI.createFrameByType("BACKDROP", displayText + "_BACKDROP", parentFrame, "", 0);
		this.displayText = new SingleStringFrame(displayText + "_NAME", parentFrame, Color.WHITE, TextJustify.LEFT, TextJustify.MIDDLE, rootList.getFrameFont());

		mapBackdrop.setHeight(mapIconSize);
		mapBackdrop.setWidth(mapIconSize);
		parentFrame.setHeight(mapIconSize);

		mapBackdrop.addSetPoint(new SetPoint(FramePoint.LEFT, parentFrame, FramePoint.LEFT, 0, 0));
		this.displayText.addSetPoint(new SetPoint(FramePoint.LEFT, mapBackdrop, FramePoint.RIGHT, 0, 0));

		parentFrame.add(mapBackdrop);
		parentFrame.add(this.displayText);
	}

	@Override
	public void remove(GameUI gameUI) {
		super.remove(gameUI);
		gameUI.remove(displayText);
		gameUI.remove(mapBackdrop);
	}

	@Override
	public void setValuesFromProperty(AbstractListItemProperty itemProperty) {
		if (!compareType(itemProperty)) return;

		ListItemFolderProperty property = (ListItemFolderProperty) itemProperty;

		displayText.setText(property.displayName);
		if (property.isGoToParent()) {
			mapBackdrop.setBackground(gameUI.loadTexture("ui\\widgets\\glues\\icon-folder-up.blp"));
		}
		else
			mapBackdrop.setBackground(gameUI.loadTexture("ui\\widgets\\glues\\icon-folder.blp"));
	}

}
