package lin.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CountDownTask {
    CountDownLatch batchCountDown;

    List<Runnable> list = new ArrayList<>();

    public void add(Runnable runnable) {
        this.list.add(runnable);
    }

    public void waitAll() throws InterruptedException {
        batchCountDown = new CountDownLatch(list.size());
        for (int i = 0; i < list.size(); i++) {
            final int idx = i;
            LinsThread.postThread(() -> {
                try {
                    list.get(idx).run();
                } finally {
                    batchCountDown.countDown();
                }
            });
        }
        batchCountDown.await();
    }
}
