package com.tomtom.di.spring;

import java.util.Map;

public class CourseIndexHolder {

    private Map<String,Course> index;
    
    public void setIndex(Map<String,Course> index) {
        this.index = index;
    }
    
    public Map<String,Course> getIndex() {
        return index;
    }
    
}
