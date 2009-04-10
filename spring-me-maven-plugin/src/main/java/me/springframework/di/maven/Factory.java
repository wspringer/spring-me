package me.springframework.di.maven;

import java.io.File;

public class Factory implements BeanFactory {

    private File contextFile;
    private String className;
    private File dotFile;
    
    public void setContextFile(File source) {
        this.contextFile = source;
    }
    
    public File getContextFile() {
        return contextFile;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getClassName() {
        return className;
    }

    public File getDotFile() {
        return dotFile;
    }
    
    public void setDotFile(File dotFile) {
        this.dotFile = dotFile;
    }
    
}
