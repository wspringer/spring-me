package me.springframework.concurrent;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class Executors {
    
    public static final Executor displayThreadExecutor(MIDlet midlet) {
        return new DisplayThreadExecutor(Display.getDisplay(midlet));
    }
    
    private static class DisplayThreadExecutor implements Executor {
        
        private Display display;

        public DisplayThreadExecutor(Display display) {
            this.display = display;
        }

        public void execute(Runnable runnable) {
            display.callSerially(runnable);
        }
        
    }

}
