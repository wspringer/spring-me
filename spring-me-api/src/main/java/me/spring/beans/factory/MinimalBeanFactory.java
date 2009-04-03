package me.spring.beans.factory;

import me.spring.beans.BeansException;


/**
 * The most basic interface of a bean factory imaginable.
 * 
 */
public interface MinimalBeanFactory {

    /**
     * Returns the bean with the specified name/id.
     * 
     * @param name The name of the bean we need.
     * @return The actual bean.
     * @throws BeansException If we fail to load this bean.
     */
    Object getBean(String name) throws BeansException;

}
