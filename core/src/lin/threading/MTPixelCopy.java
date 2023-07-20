package lin.threading;

public class MTPixelCopy {
	private final int w;
	private final int h;
	private final PixelReader reader;
	private DecodedBitmap handler;

	public MTPixelCopy(int w, int h, PixelReader reader) {
		this.w = w;
		this.h = h;
		this.reader = reader;
		this.handler = new DecodedBitmap(null,null, w, h, 4);
	}

	public DecodedBitmap doCopy() {
		return process(0, 0, w, h, null, 0, 0);
	}

	public DecodedBitmap process(final int startX, int startY, final int w, int h, int[] rgbArray,
			int offset, int scansize) {
		if (rgbArray == null) {
			rgbArray = new int[offset + h * scansize];
		}
		CountDownTask task = new CountDownTask();
		final int[] data = rgbArray;
		int off = offset;
		for (int y = startY; y < startY + h; y++, off += scansize) {
			final int i = off;
			final int offsetY = y;
			task.add(() -> {
				pixiesRead(data, i, startX, w, offsetY);
			});
		}
		try {
			task.waitAll();
			handler.complete();
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return (DecodedBitmap) handler;
	}

	void pixiesRead(int[] data, int i, final int startX, final int w, final int y) {
		for (int x = startX; x < startX + w; x++, i++) {
			data[i] = handler.pixelRead(i, x, y, reader.read(x, y));
		}
	}


}

