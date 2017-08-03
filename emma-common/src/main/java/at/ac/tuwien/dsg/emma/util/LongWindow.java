package at.ac.tuwien.dsg.emma.util;

import java.util.Arrays;

/**
 * A window of long values. You can just keep adding them, the window will extend.
 */
public class LongWindow {

    private long[] values;

    private int capacity;
    private int head;

    public LongWindow(int window) {
        this.values = new long[window];
        this.capacity = window;
    }

    public void add(long value) {
        values[head] = value;
        head = (head + 1) % capacity;
    }

    public double average() {
        double d = 0;

        for (long value : values) {
            d += (value / (double) capacity);
        }

        return d;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

}
