package me.springframework.di.spring;

public class Bean5<T> {

    private T t;

    public Bean5(T t) {
        this.t = t;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

}
