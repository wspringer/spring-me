package me.springframework.di.maven;

import java.io.File;


public class ChainingBeanFactory implements BeanFactory {

    private BeanFactory first;

    private BeanFactory second;
    
    public ChainingBeanFactory(BeanFactory first, BeanFactory second) {
        this.first = first;
        this.second = second;
    }

    public String getClassName() {
        String result = first.getClassName();
        if (result != null) {
            return result;
        } else {
            return second.getClassName();
        }
    }

    public File getDotFile() {
        File result = first.getDotFile();
        if (result != null) {
            return result;
        } else {
            return second.getDotFile();
        }
    }

    public File getContextFile() {
        File result = first.getContextFile();
        if (result != null) {
            return result;
        } else {
            return second.getContextFile();
        }
    }

}
