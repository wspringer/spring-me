package me.springframework.sample.cmdline;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import me.springframework.sample.api.Movie;

@XStreamAlias("movie")
public class XStreamMovie implements Movie {

    private String director;
    private String title;
    private int year;
    
    public String getDirector() {
        return director;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

}
