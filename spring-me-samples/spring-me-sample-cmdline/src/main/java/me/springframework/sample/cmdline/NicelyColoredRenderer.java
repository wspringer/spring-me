package me.springframework.sample.cmdline;

import java.io.PrintStream;
import java.util.List;

import me.springframework.sample.api.Movie;


public class NicelyColoredRenderer implements MovieListRenderer {

    public String getTypeName() {
        return "With colors!";
    }

    public void print(List<? extends Movie> movies, PrintStream out) {
        for (Movie movie : movies) {
            System.out.println("\u001b[1;44m" + movie.getTitle() + "\u001b[m (" + movie.getYear() + ")");
        }
    }

}
