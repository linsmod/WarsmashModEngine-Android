package lin.threading;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MtPixelTaskGroup {
	private final int w;
	private final int h;
	private List<MtPixelTask> list;
	private PixelReader reader;

	public MtPixelTaskGroup(PixelReader reader, int w, int h, List<MtPixelTask> list) {
		this.reader = reader;
		this.w = w;
		this.h = h;
		this.list = list;
	}

	public void readAsync(PromiseFunctionGeneric<RgbaImageBuffer> runnable) {
		LinsThread.bgTask(() -> read()).thenUITask(runnable);
	}

	public RgbaImageBuffer read() {
		int[] rgbArray = new int[w * h];
		CountDownLatch latch = new CountDownLatch(list.size());
		RgbaImageBuffer buffer = new RgbaImageBuffer(rgbArray, w, h, 4);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).run(buffer, latch, true);
		}
		try {
			latch.await();
			buffer.complete();
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return buffer;
	}
}
