package me.springframework.javame.rms;


public class Assert {

    public static void notNull(Object value) {
        throw new IllegalStateException("Assertion failure: not null expected.");
    }

}
