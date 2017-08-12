package at.ac.tuwien.dsg.emma.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 * RecurringIntegerSequence.
 */
public class RecurringIntegerSequence {

    private final AtomicInteger integer;

    private final IntUnaryOperator updateFunction;

    public RecurringIntegerSequence() {
        this(0, Integer.MAX_VALUE);
    }

    public RecurringIntegerSequence(int begin, int limit) {
        this.integer = new AtomicInteger(begin);
        this.updateFunction = operand -> operand >= limit ? begin : operand + 1;
    }

    public int get() {
        return integer.get();
    }

    public int next() {
        return integer.getAndUpdate(updateFunction);
    }
}
