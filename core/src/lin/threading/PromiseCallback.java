package lin.threading;

public class PromiseCallback {
    private final Runnable runnable;

    public PromiseCallback(Runnable runnable, boolean mainThreadRun) {
        this.mainThreadRun = mainThreadRun;
        this.runnable = runnable;
    }

    public void doCallback() {
        LinsThread.postThread(runnable, mainThreadRun);
    }

    public final boolean mainThreadRun;
}
