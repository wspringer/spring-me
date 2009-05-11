package me.springframework.adapter;

import me.spring.beans.BeansException;
import me.spring.beans.factory.MinimalBeanFactory;

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
