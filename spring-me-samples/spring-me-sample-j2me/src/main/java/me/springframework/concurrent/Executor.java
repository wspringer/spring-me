package me.springframework.concurrent;

public interface Executor {
    
    void execute(Runnable runnable);

}
