package com.etheller.warsmash.parsers.fdf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.IntMap;

public class FontGeneratorHolder {
	private final FreeTypeFontGenerator generator;
	private final IntMap<BitmapFont> sizeToFont;
	private final static String characters;
	static {
		characters = String.join("", Gdx.files.internal("characters.txt").readString().split("\n"));
	}
	public FontGeneratorHolder(final FreeTypeFontGenerator generator) {
		this.generator = generator;
		this.sizeToFont = new IntMap<>();
	}

	public BitmapFont generateFont(final FreeTypeFontParameter parameter) {
		BitmapFont font = this.sizeToFont.get(parameter.size);
		if (font == null) {
			parameter.characters += characters;
			font = this.generator.generateFont(parameter);
			this.sizeToFont.put(parameter.size, font);
		}
		return font;
	}

	public void dispose() {
		this.generator.dispose();
		// TODO maybe dispose the fonts
	}
}
