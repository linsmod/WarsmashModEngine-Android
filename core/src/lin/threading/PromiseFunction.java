package lin.threading;

@FunctionalInterface
public interface PromiseFunction {
    void run(PromiseContext ctx) throws Exception;
}

