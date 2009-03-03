package me.springframework.gwtsample.client.util;

public interface ObjectChangeListener<T> {

    void onObjectChanged(Object source, T object);

}
