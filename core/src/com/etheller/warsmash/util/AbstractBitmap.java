package com.etheller.warsmash.util;

import java.nio.ByteBuffer;

public abstract class AbstractBitmap {

	public abstract int getHeight();


	public abstract int getWidth();

	public abstract ByteBuffer getBuffer();
}
