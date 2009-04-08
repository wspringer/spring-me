package me.springframework.sample.api;

public class BaseMovie implements Movie {

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
    
    public void setDirector(String director) {
        this.director = director;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
}
