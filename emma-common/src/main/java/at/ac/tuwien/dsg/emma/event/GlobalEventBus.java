package at.ac.tuwien.dsg.emma.event;

/**
 * GlobalEventBus.
 */
public final class GlobalEventBus extends EventBus {

    private GlobalEventBus() {
    }

    private static GlobalEventBus instance;

    public static GlobalEventBus getInstance() {
        if (instance == null) {
            instance = new GlobalEventBus();
        }
        return instance;
    }
}

