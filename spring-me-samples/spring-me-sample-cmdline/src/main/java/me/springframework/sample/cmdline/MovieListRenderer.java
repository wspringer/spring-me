package me.springframework.sample.cmdline;

import java.io.PrintStream;
import java.util.List;

import me.springframework.sample.api.Movie;

public interface MovieListRenderer {

    String getTypeName();
    
    void print(List<? extends Movie> movies, PrintStream out);
    
}
