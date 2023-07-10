package lin.threading;

import com.badlogic.gdx.Gdx;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LinsThread {
    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Instantiates the queue of Runnables as a LinkedBlockingQueue
    private static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MINUTES;

    // Creates a thread pool manager
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2,       // Initial pool size
            NUMBER_OF_CORES,       // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue, new CustomThreadFactory()
    );

    private static class CustomThreadFactory implements ThreadFactory {

        private AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            String threadName = "BGThread_" + count.addAndGet(1);
//            System.out.println(threadName);
//            t.setDaemon(true);
            t.setName(threadName);
            return t;
        }
    }

    public static void postThread(Runnable runnable) {
        postThread(runnable, false);
    }

    public static void postThread(Runnable runnable, boolean mainThread) {
        if (mainThread)
            Gdx.app.postRunnable(runnable);
        else
            threadPoolExecutor.submit(runnable);
    }


    public static Promise uiTask(Runnable runnable) {
        return promise(runnable, true);
    }

    public static Promise bgTask(Runnable runnable) {
        return promise(runnable, false);
    }

    //
    public static Promise uiTask(PromiseFunction runnable) {
        return promise(runnable, true);
    }

    public static Promise bgTask(PromiseFunction runnable) {
        return promise(runnable, false);
    }

    public static <T> PromiseGeneric<T> bgTask(PromiseAction<T> runnable) {
        return promise(runnable, false);
    }

    public static <T> PromiseGeneric<T> uiTask(PromiseAction<T> runnable) {
        return promise(runnable, true);
    }

    static <T> PromiseGeneric<T> promise(PromiseAction<T> runnable, boolean runInMainThread) {
        return new PromiseGeneric<T>(runnable, runInMainThread, null);
    }

    static Promise promise(PromiseFunction runnable, boolean runInMainThread) {
        return new Promise(runnable, runInMainThread, null);
    }

    static Promise promise(Runnable runnable, boolean runInMainThread) {
        return new Promise(runnable, runInMainThread, null);
    }
}

