package me.springframework.javame.rms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    public static void closeQuietly(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch(IOException ioe) {
                // Quietly
            }
        }
    }

    public static void closeQuietly(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch(IOException ioe) {
                // Quietly
            }
        }
    }
    
}
