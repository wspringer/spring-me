package me.springframework.sample.cmdline;

import me.spring.beans.BeansException;
import me.spring.beans.factory.MinimalBeanFactory;

public class DynamicMinimalBeanFactory implements MinimalBeanFactory {

    private MinimalBeanFactory beanFactory;
    
    private static final String FACTORY_PROPERTY = "spring.me.factory";
    
    public DynamicMinimalBeanFactory() {
        try {
            Class<?> cl = Class.forName(System.getProperty(FACTORY_PROPERTY));
            beanFactory = (MinimalBeanFactory) cl.newInstance();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public Object getBean(String name) throws BeansException {
        return beanFactory.getBean(name);
    }

}
