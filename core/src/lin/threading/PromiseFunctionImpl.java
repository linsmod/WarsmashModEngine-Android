package lin.threading;

import java.util.concurrent.atomic.AtomicInteger;

public class PromiseFunctionImpl implements PromiseFunction {
    private final Runnable body;
    private final String threadType;
    private final boolean mainThreadRun;
    private static AtomicInteger count = new AtomicInteger(0);
    private final int id;
    private final PromiseContext context;
    private Throwable callStack;

    public PromiseFunctionImpl(PromiseContext ctx, Runnable runnable, boolean mainThreadRun) {
        this.body = runnable;
        this.context = ctx;
        this.mainThreadRun = mainThreadRun;
        this.threadType = mainThreadRun ? "uiThread" : "bgThread";
        this.id = count.incrementAndGet();

    }

    @Override
    public void run(PromiseContext ctx) {
        try {
            this.body.run();
            if (ctx.getStatus() == "pending")
                ctx.resolve(null);
            if (ctx.getStatus() == "rejected") {
                System.out.println("a threading task is rejected in [" + Thread.currentThread().getName() + "]");
                return;
            }
            PromiseFunctionImpl next = ctx.nextTask();
            if (next != null)
                next.postThread();
        } catch (Exception error) {
            ctx.setError(error);
            System.out.println("a threading task is failed in [" + Thread.currentThread().getName() + "]");
            if (!this.mainThreadRun) {
                error.printStackTrace();
                this.callStack.printStackTrace();
            } else {
                throw error;
            }
        }
    }

    public void postThread() {
        this.callStack = new Exception("PromiseFunctionImpl$postThread");
        LinsThread.postThread(() -> run(this.context), mainThreadRun);
    }
}
