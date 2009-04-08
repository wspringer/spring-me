package me.springframework.sample.javame;

import java.util.Vector;

import me.springframework.javame.rms.RecordStoreTemplate;
import me.springframework.sample.api.MovieFinder;

public class RecordStoreMovieFinder implements MovieFinder {

    private RecordStoreTemplate template;
    
    public Vector findAll() {
        return template.query(new MovieCodec());
    }
    
    public void setTemplate(RecordStoreTemplate template) {
        this.template = template;
    }
    
    public RecordStoreTemplate getTemplate() {
        return template;
    }

}
