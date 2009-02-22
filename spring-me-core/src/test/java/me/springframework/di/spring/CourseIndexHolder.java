package me.springframework.di.spring;

import java.util.Map;

public class CourseIndexHolder {
    
    private Map<String, Course> courseByKeyword;
    
    public Course getByKeyword(String keyword) {
        return courseByKeyword.get(keyword);
    }
    
    public Map<String, Course> getIndex() {
        return courseByKeyword;
    }
    
    public void setIndex(Map<String, Course> courseByKeyword) {
        this.courseByKeyword = courseByKeyword;
    }

}
