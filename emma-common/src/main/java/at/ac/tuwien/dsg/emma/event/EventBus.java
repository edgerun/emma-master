package at.ac.tuwien.dsg.emma.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventBus.
 */
public class EventBus {

    private Map<Class, List<EventHandler>> eventHandlers;

    public EventBus() {
        this.eventHandlers = new HashMap<>();
    }

    public <E> void register(Class<E> eventClass, EventHandler<E> eventHandler) {
        List<EventHandler> handlers = this.eventHandlers.computeIfAbsent(eventClass, k -> new ArrayList<>());
        if (!handlers.contains(eventHandler)) {
            handlers.add(eventHandler);
        }
    }

    public void unregister(EventHandler eventHandler) {
        eventHandlers.values().forEach(list -> list.remove(eventHandler));
    }

    public void fire(Object event) {
        List<EventHandler> handlers = getMatchingHandlers(event);

        for (EventHandler handler : handlers) {
            try {
                handler.onEvent(event);
            } catch (Exception e) {
                try {
                    handler.onException(e);
                } catch (Exception handlerException) {
                    handlerException.printStackTrace(System.err);
                }
            }
        }
    }

    private List<EventHandler> getMatchingHandlers(Object event) {
        Class<?> eventClass = event.getClass();

        List<EventHandler> matching = new ArrayList<>();

        for (Map.Entry<Class, List<EventHandler>> entry : eventHandlers.entrySet()) {
            Class<?> handledClass = entry.getKey();

            if (handledClass.isAssignableFrom(eventClass)) {
                matching.addAll(entry.getValue());
            }
        }

        return matching;
    }

}
