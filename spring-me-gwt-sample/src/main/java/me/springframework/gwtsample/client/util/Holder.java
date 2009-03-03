package me.springframework.gwtsample.client.util;

public class Holder<T> implements Accessor<T> {

    private final ChangeSupport<T> changeSupport = new ChangeSupport<T>();
    private T item;

    public Holder() {
    }

    public Holder(final T item) {
        set(item);
    }

    public void set(final T item) {
        this.item = item;
    }

    public T get() {
        return item;
    }

    public void fireChangeEvent(final Object source) {
        changeSupport.notifyListeners(source, get());
    }

    public final void addListener(final ObjectChangeListener<T> listener) {
        changeSupport.addListener(listener);
    }

    public final void removeListener(final ObjectChangeListener<T> listener) {
        changeSupport.removeListener(listener);
    }

}
