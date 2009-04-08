package me.springframework.sample.javame;

import java.util.Vector;

import me.springframework.sample.api.MovieFinder;

public class InMemoryMovieStore implements MovieFinder {

    Vector movies;
    
    public Vector findAll() {
        return movies;
    }

    public void setMovies(Vector movies) {
        this.movies = movies;
    }
    
    public Vector getMovies() {
        return movies;
    }
    
}
