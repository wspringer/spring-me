package me.spring.beans;

public class BeansException extends RuntimeException {

    private static final long serialVersionUID = 1794130445396635028L;

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeansException(String message) {
        super(message);
    }

    public BeansException(Throwable cause) {
        super(cause);
    }

}
