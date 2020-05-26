package io.edgerun.emma.util;


import java.util.AbstractList;

/**
 * AppendList.
 */
public class AppendList<T> extends AbstractList<T> {

    private int capacity;
    private T[] elementData;

    private int head;
    private int size;

    public AppendList(int capacity) {
        this.capacity = capacity;
        this.elementData = newArray(capacity);
    }

    @Override
    public T get(int index) {
        int i = size == capacity ? (index + head) % capacity : index;

        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException();
        }

        return elementData[i];
    }

    @Override
    public boolean add(T t) {
        elementData[head] = t;

        head = (head + 1) % capacity;

        if (size < capacity) {
            size++;
        }

        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < elementData.length; i++) {
            elementData[i] = null;
        }
        head = 0;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private T[] newArray(int capacity) {
        return (T[]) new Object[capacity];
    }
}
