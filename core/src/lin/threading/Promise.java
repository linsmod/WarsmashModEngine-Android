package lin.threading;

public class Promise {
    PromiseContext context = null;

    public Promise(Runnable runnable, boolean mainThreadRun, PromiseContext context) {
        this((Object) runnable, mainThreadRun, true, context);
    }

    public Promise(PromiseFunction runnable, boolean mainThreadRun, PromiseContext context) {
        this((Object) runnable, mainThreadRun, true, context);
    }

    Promise(Object runnable, boolean mainThreadRun, boolean doPostThread, PromiseContext context) {
        if (context == null)
            this.context = new PromiseContext();
        else
            this.context = context;

        PromiseFunctionImpl impl = null;
        if (runnable instanceof PromiseFunction) {
            var fn = (PromiseFunction) runnable;
            impl = wrapImpl(this.context, fn, mainThreadRun);
        } else {
            impl = wrapImpl(this.context, (Runnable) runnable, mainThreadRun);
        }
        if (doPostThread) {
            impl.postThread();
        } else {
            context.addTask(impl);
        }
    }

    static PromiseFunctionImpl wrapImpl(PromiseContext ctx, PromiseFunction runnable, boolean mainThreadRun) {
        return new PromiseFunctionImpl(ctx, () -> {
            try {
                runnable.run(ctx);
            } catch (Exception e) {
                e.printStackTrace();throw new RuntimeException(e);
            }
        }, mainThreadRun);
    }

//    {
//        if (ctx.getStatus() == "pending") {
//            ctxImpl.addCallback(runnable, mainThreadRun);
//        } else if (ctx.getStatus() == "resolved") {
//            ctx.resolve(LinsThread.promise(runnable, mainThreadRun));
//        } else if (ctx.getStatus() == "rejected") {
//            try {
//                ctx.reject(ctx.getData(), ctx.getReason());
//            } catch (Exception e) {
//                e.printStackTrace();throw new RuntimeException(e);
//            }
//        }
//    }

    static PromiseFunctionImpl wrapImpl(PromiseContext ctx, Runnable runnable, boolean mainThreadRun) {
        return new PromiseFunctionImpl(ctx, runnable, mainThreadRun);
    }

    public Promise thenBGTask(Runnable runnable) {
        return then(runnable, false, this.context);
    }

    public Promise thenUITask(Runnable runnable) {
        return then(runnable, true, this.context);
    }

    public Promise thenBGTask(PromiseFunction runnable) {
        return then(runnable, false, this.context);
    }

    public Promise thenUITask(PromiseFunction runnable) {
        return then(runnable, true, this.context);
    }

    public Promise then(Runnable runnable, boolean mainThreadRun, PromiseContext context) {
        return new Promise(runnable, mainThreadRun, false, context);
    }

    public Promise then(PromiseFunction runnable, boolean mainThreadRun, PromiseContext context) {
        return new Promise(runnable, mainThreadRun, false, context);
    }
}
