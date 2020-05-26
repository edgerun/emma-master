package io.edgerun.emma.event;

/**
 * EventHandler.
 */
public interface EventHandler<E> {
    void onEvent(E event) throws Exception;

    default void onException(Exception e) {
        e.printStackTrace(System.err);
    }
}
