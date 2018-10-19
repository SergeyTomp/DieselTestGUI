package fi.stardex.sisu.pdf;

/**
 * @author rom8
 */
@FunctionalInterface
public interface ExceptionalConsumer<T, E extends Exception> {

    void accept(T t) throws E;
}
