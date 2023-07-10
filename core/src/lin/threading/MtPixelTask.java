package lin.threading;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MtPixelTask {
	private final PixelReader reader;
	public int w;
	public int h;
	public int offset;
	public int pixels; //batch size

	public MtPixelTask(PixelReader reader, int w, int h, int i, int pixels) {
		this.w = w;
		this.h = h;
		this.offset = i;
		this.pixels = pixels;
		this.reader = reader;
	}

	public static MtPixelTaskGroup create(PixelReader reader, int w, int h, int pixesPerBatch) {
		List<MtPixelTask> list = new ArrayList<>();
		int i = 0;
		MtPixelTask task = null;
		int total = w * h;
		if (total > pixesPerBatch) {
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					if (i % pixesPerBatch == 0) {
						task = new MtPixelTask(reader, w, h, i, pixesPerBatch);
						list.add(task);
					}
					i++;
				}
			}
		}
		if (total - i > 0) {
			task = new MtPixelTask(reader, w, h, i, total - i);
			list.add(task);
		}
		return new MtPixelTaskGroup(reader, w, h, list);
	}

	public void run(RgbaImageBuffer buffer, CountDownLatch latch, boolean postThread) {
		if (postThread) {
			LinsThread.postThread(() -> this.read(buffer, latch));
		}
		else {
			this.read(buffer, latch);
		}
	}

	void read(RgbaImageBuffer buffer, CountDownLatch latch) {
		try {
			int i = offset;
			while (i < offset + pixels) {
				final int x = i % w;
				final int y = i / w;
				buffer.pixelRead(i, x, y, reader.read(x, y));
				i++;
			}
		} finally {
			latch.countDown();
		}
	}
}

