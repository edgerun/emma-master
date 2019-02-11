package at.ac.tuwien.dsg.emma.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * EventBusTest.
 */
public class EventBusTest {

    @Test
    public void test() throws Exception {
        EventBus eventBus = new EventBus();

        AtomicInteger eventCall = new AtomicInteger();
        AtomicInteger eventACall = new AtomicInteger();
        AtomicInteger eventAACall = new AtomicInteger();

        eventBus.register(Object.class, event -> {
            System.out.println("called wildcard handler");
            eventCall.incrementAndGet();
        });
        eventBus.register(EventA.class, event -> {
            System.out.println("called EventA 1");
            eventACall.incrementAndGet();
        });
        eventBus.register(EventA.class, event -> {
            System.out.println("called EventA 2");
            eventACall.incrementAndGet();
        });
        eventBus.register(EventAA.class, event -> {
            System.out.println("called EventAA");
            eventAACall.incrementAndGet();
        });
        eventBus.register(EventB.class, event -> {
            System.out.println("called EventB");
            fail();
        });

        eventBus.fire(new EventA());

        assertEquals(1, eventCall.get());
        assertEquals(2, eventACall.get());
        assertEquals(0, eventAACall.get());
    }

    public static class EventA {

    }

    public static class EventAA extends EventA {

    }

    public static class EventB {

    }
}