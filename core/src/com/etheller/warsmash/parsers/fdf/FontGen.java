package com.etheller.warsmash.parsers.fdf;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;

public class FontGen extends FreeTypeFontGenerator {
	/**
	 * {@link #FreeTypeFontGenerator(FileHandle, int)}
	 *
	 * @param fontFile
	 */
	public FontGen(FileHandle fontFile) {
		super(fontFile);
	}

	@Override
	protected BitmapFont newBitmapFont(BitmapFont.BitmapFontData data, Array<TextureRegion> pageRegions, boolean integer) {
		return super.newBitmapFont(data, pageRegions, integer);
	}
	class BitmapFontDataProxy extends BitmapFont.BitmapFontData{
		@Override
		public void getGlyphs(GlyphLayout.GlyphRun run, CharSequence str, int start, int end, BitmapFont.Glyph lastGlyph) {
			for (int i = 0; i < str.length(); i++) {

			}
			super.getGlyphs(run, str, start, end, lastGlyph);
		}
	}
}
