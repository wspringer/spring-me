package me.springframework.sample.cmdline;

import java.util.List;

import me.springframework.sample.api.Movie;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("movies")
public class XStreamMovies {

    @XStreamImplicit(itemFieldName="movie")
    private List<XStreamMovie> movies;
    
    List<? extends Movie> getMovies() {
        return movies;
    }
    
}
