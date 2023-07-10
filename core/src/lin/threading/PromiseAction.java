package lin.threading;

@FunctionalInterface
public interface PromiseAction<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T invoke();
}
interface PromiseActionObject extends PromiseAction<Object> {

}
