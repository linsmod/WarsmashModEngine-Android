package lin.threading;


public class PromiseGeneric<T> {
    PromiseContext context = null;

    public PromiseGeneric(Runnable runnable, boolean mainThreadRun, PromiseContext context) {
        this((Object) runnable, mainThreadRun, true, context);
    }

    public PromiseGeneric(PromiseAction<T> runnable, boolean mainThreadRun, PromiseContext context) {
        this(runnable, mainThreadRun, true, context);
    }

    PromiseGeneric(PromiseAction<T> fn, boolean mainThreadRun, boolean doPostThread, PromiseContext context) {
        if (context == null)
            this.context = new PromiseContext();
        else
            this.context = context;
        var impl = wrapImpl(this.context, fn, mainThreadRun);
        if (doPostThread) {
            impl.postThread();
        } else {
            context.addTask(impl);
        }
    }
    PromiseGeneric(PromiseFunctionGeneric<T> fn, boolean mainThreadRun, boolean doPostThread, PromiseContext context) {
        if (context == null)
            this.context = new PromiseContext();
        else
            this.context = context;
        var impl = wrapImpl(this.context, fn, mainThreadRun);
        if (doPostThread) {
            impl.postThread();
        } else {
            context.addTask(impl);
        }
    }

    PromiseGeneric(Object runnable, boolean mainThreadRun, boolean doPostThread, PromiseContext context) {
        if (context == null)
            this.context = new PromiseContext();
        else
            this.context = context;
        PromiseFunctionImpl impl = null;
        if (runnable instanceof PromiseFunction) {
            var fn = (PromiseFunction) runnable;
            impl = Promise.wrapImpl(this.context, fn, mainThreadRun);
        } else {
            impl = wrapImpl(this.context, (Runnable) runnable, mainThreadRun);
        }
        if (doPostThread) {
            impl.postThread();
        } else {
            context.addTask(impl);
        }
    }

    @SuppressWarnings("NewApi")
    static <T> PromiseFunctionImpl wrapImpl(PromiseContext ctx, PromiseAction<T> runnable, boolean mainThreadRun) {
        return new PromiseFunctionImpl(ctx, () -> {
            try {
                ctx.resolve(runnable.invoke());
            } catch (Exception e) {
                e.printStackTrace();throw new RuntimeException(e);
            }
        }, mainThreadRun);
    }
    static <T> PromiseFunctionImpl wrapImpl(PromiseContext ctx, PromiseFunctionGeneric<T> runnable, boolean mainThreadRun) {
        return new PromiseFunctionImpl(ctx, () -> {
            try {
                runnable.invoke((T)ctx.getData());
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

    public PromiseGeneric<T> thenBGTask(Runnable runnable) {
        return then(runnable, false, this.context);
    }

    public PromiseGeneric<T> thenUITask(Runnable runnable) {
        return then(runnable, true, this.context);
    }

    public PromiseGeneric<T> thenBGTask(PromiseAction<T> runnable) {
        return then(runnable, false, this.context);
    }

    public PromiseGeneric<T> thenBGTask(PromiseFunctionGeneric<T> runnable) {
        return then(runnable, false, this.context);
    }

    public PromiseGeneric<T> thenBGTask(PromiseFunction runnable) {
        return then(runnable, false, this.context);
    }

    public PromiseGeneric<T> thenBGTask(PromiseActionObject runnable) {
        return then(runnable, false, this.context);
    }


    public PromiseGeneric<T> thenUITask(PromiseAction<T> runnable) {
        return then(runnable, true, this.context);
    }

    public PromiseGeneric<T> thenUITask(PromiseFunctionGeneric<T> runnable) {
        return then(runnable, true, this.context);
    }

    public PromiseGeneric<T> then(Runnable runnable, boolean mainThreadRun, PromiseContext context) {
        return new PromiseGeneric(runnable, mainThreadRun, false, context);
    }

    public PromiseGeneric<T> then(PromiseAction<T> runnable, boolean mainThreadRun, PromiseContext context) {
        return new PromiseGeneric(runnable, mainThreadRun, false, context);
    }

    public PromiseGeneric<T> then(PromiseFunction runnable, boolean mainThreadRun, PromiseContext context) {
        return new PromiseGeneric(runnable, mainThreadRun, false, context);
    }

    public PromiseGeneric<T> then(PromiseActionObject runnable, boolean mainThreadRun, PromiseContext context) {
        return new PromiseGeneric(runnable, mainThreadRun, false, context);
    }
    public PromiseGeneric<T> then(PromiseFunctionGeneric<T> runnable, boolean mainThreadRun, PromiseContext context) {
        return new PromiseGeneric(runnable, mainThreadRun, false, context);
    }
}
