package io.edgerun.emma.util;

import java.util.ArrayList;
import java.util.List;

/**
 * TimeSeries.
 */
public class TimeSeries<T> {

    private List<T> data;
    private List<Long> time;

    public TimeSeries() {
        this.data = new ArrayList<>();
        this.time = new ArrayList<>();
    }

    public TimeSeries(int initialCapacity) {
        this.data = new ArrayList<>(initialCapacity);
        this.time = new ArrayList<>(initialCapacity);
    }

    public void add(T point) {
        add(System.currentTimeMillis(), point);
    }

    public void add(long ms, T point) {
        time.add(ms);
        data.add(point);
    }

    public int size() {
        return data.size();
    }

    public long getTime(int x) {
        return time.get(x);
    }

    public T getData(int x) {
        return data.get(x);
    }

    public List<T> getData() {
        return data;
    }

    public List<Long> getTime() {
        return time;
    }

    public void clear() {
        data.clear();
        time.clear();
    }
}
