package lin.threading;

public interface PromiseFunctionGeneric<T> {
    void invoke(T ctx) throws Exception;
}
