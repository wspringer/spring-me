package me.springframework.gwtsample.client.util;

import java.util.ArrayList;

public class ChangeSupport<T> {

    private final ArrayList<ObjectChangeListener<T>> listeners = new ArrayList<ObjectChangeListener<T>>();

    public final void addListener(final ObjectChangeListener<T> listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public final void removeListener(final ObjectChangeListener<T> listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public final void notifyListeners(final Object source, final T o) {
        for (final ObjectChangeListener<T> listener : listeners) {
            listener.onObjectChanged(source, o);
        }
    }

}
