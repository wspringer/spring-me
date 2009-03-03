package me.springframework.gwtsample.client.util;

public interface Accessor<T> {

    public void set(final T item);

    public T get();

}
