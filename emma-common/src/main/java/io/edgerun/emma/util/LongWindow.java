package io.edgerun.emma.util;

import java.util.Arrays;

/**
 * A window of long values. You can just keep adding them, the window will extend.
 */
public class LongWindow {

    private long[] values;

    private int capacity;
    private int head;
    private int count;

    public LongWindow(int window) {
        this.values = new long[window];
        this.capacity = window;
    }

    public void add(long value) {
        values[head] = value;
        head = (head + 1) % capacity;

        if (count < capacity) {
            count++;
        }
    }

    public int size() {
        return capacity;
    }

    public int count() {
        return count;
    }

    public double average() {
        double d = 0;

        for (int i = 0; i < count; i++) {
            long value = values[i];
            d += value / (double) count;
        }

        return d;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

}
