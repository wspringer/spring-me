package me.springframework.sample.cmdline;

import java.io.PrintStream;
import java.util.List;

import me.springframework.sample.api.Movie;

public class TitlesOnlyRenderer implements MovieListRenderer {

    public String getTypeName() {
        return "Titles only";
    }

    public void print(List<? extends Movie> movies, PrintStream out) {
        for (Movie movie : movies) {
            System.out.println(movie.getTitle());
        }
    }

}
