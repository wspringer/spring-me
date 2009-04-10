package me.springframework.di.maven;

import java.io.File;

public interface BeanFactory {
    
    File getContextFile();
    
    String getClassName();
    
    File getDotFile();

}
