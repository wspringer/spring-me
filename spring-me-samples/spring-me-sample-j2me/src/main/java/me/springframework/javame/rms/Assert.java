package me.springframework.javame.rms;

public class Assert {

    public static void notNull(Object value) {
        if (value == null) {
            throw new IllegalStateException("Assertion failure: not null expected.");
        }
    }

}
