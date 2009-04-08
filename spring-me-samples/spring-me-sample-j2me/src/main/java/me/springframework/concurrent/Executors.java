package me.springframework.concurrent;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class Executors {
    
    public static final Executor displayThreadExecutor(MIDlet midlet) {
        return new DisplayThreadExecutor(midlet);
    }
    
    public static final Executor currentThreadExecutor() {
        return new Executor() {
            public void execute(Runnable runnable) {
                runnable.run();
            }
        };
    }
    
    private static class DisplayThreadExecutor implements Executor {
        
        private MIDlet midlet;
        
        public DisplayThreadExecutor(MIDlet midlet) {
            this.midlet = midlet;
        }

        public void execute(Runnable runnable) {
            Display display = Display.getDisplay(midlet);
            if (display != null) {
                display.callSerially(runnable);
            } else {
                runnable.run();
            }
        }
        
    }

    public static Executor newThreadExecutor() {
        return new Executor() {
            public void execute(final Runnable runnable) {
                new Thread(runnable).start();
            }
        };
    }

}
