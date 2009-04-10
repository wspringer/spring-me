package me.springframework.adapter;

import me.spring.beans.BeansException;
import me.spring.beans.factory.MinimalBeanFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class ClassicSpringAdapter implements MinimalBeanFactory {

    private BeanFactory factory;

    public static final String APPLICATION_CONTEXT_PROPERTY = "spring.me.context";

    public ClassicSpringAdapter() {
        this(new ClassPathResource(System.getProperty(APPLICATION_CONTEXT_PROPERTY)));
    }

    public ClassicSpringAdapter(Resource resource) {
        this.factory = new XmlBeanFactory(resource);
    }

    public ClassicSpringAdapter(BeanFactory factory) {
        this.factory = factory;
    }

    public Object getBean(String name) throws BeansException {
        try {
            return factory.getBean(name);
        } catch (org.springframework.beans.BeansException be) {
            throw new BeansException(be);
        }
    }

}
