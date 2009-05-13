/**
 * Copyright (C) 2009 Original Authors
 *
 * This file is part of Spring ME.
 *
 * Spring ME is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * Spring ME is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Spring ME; see the file COPYING. If not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library. Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module. An independent module is a module which is not derived from or
 * based on this library. If you modify this library, you may extend this
 * exception to your version of the library, but you are not obligated to
 * do so. If you do not wish to do so, delete this exception statement
 * from your version.
 */
package me.springframework.adapter;

import me.springframework.beans.BeansException;
import me.springframework.beans.factory.MinimalBeanFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * A {@link MinimalBeanFactory} wrapping an ordinary 'classic' Spring BeanFactory. This class allows
 * you to use normal Spring BeanFactories in cases where your code depends on an implementation of
 * the {@link MinimalBeanFactory}; as a consequence, you can have <em>the same application</em>
 * running both from a Spring ME Generated bean factory, and from a normal bean factory.
 * 
 */
public class ClassicSpringAdapter implements MinimalBeanFactory {

    /**
     * The {@link BeanFactory} to wrap.
     */
    private BeanFactory factory;

    /**
     * The property name of the system property that will hold the name of the Spring XML
     * application context file. Used in the {@linkplain #ClassicSpringAdapter() default
     * constructor}.
     */
    public static final String APPLICATION_CONTEXT_PROPERTY = "spring.me.context";

    /**
     * Constructs a new instance, taking the name of the XML application context from a system
     * property with the name of {@linkplain #APPLICATION_CONTEXT_PROPERTY this constant}.
     */
    public ClassicSpringAdapter() {
        this(new ClassPathResource(System.getProperty(APPLICATION_CONTEXT_PROPERTY)));
    }

    /**
     * Constructs a {@link ClassicSpringAdapter} from the XML application context file to which this
     * <code>resource</code> is pointing.
     * 
     * @param resource The {@link Resource} pointing to the XML application context file holding the
     *            bean configuration.
     */
    public ClassicSpringAdapter(Resource resource) {
        this.factory = new XmlBeanFactory(resource);
    }

    /**
     * Constructs a new instances by wrapping the {@link BeanFactory} passed in.
     * 
     * @param factory The {@link BeanFactory} to wrap.
     */
    public ClassicSpringAdapter(BeanFactory factory) {
        this.factory = factory;
    }

    /*
     * (non-Javadoc)
     * @see me.spring.beans.factory.MinimalBeanFactory#getBean(java.lang.String)
     */
    public Object getBean(String name) throws BeansException {
        try {
            return factory.getBean(name);
        } catch (org.springframework.beans.BeansException be) {
            throw new BeansException(be);
        }
    }

}
