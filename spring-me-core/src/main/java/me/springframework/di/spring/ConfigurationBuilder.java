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
package me.springframework.di.spring;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.springframework.di.Configuration;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

import com.agilejava.blammo.LoggingKitAdapter;
import com.thoughtworks.qdox.JavaDocBuilder;

/**
 * Creates {@link Configuration} instances from Spring configurations.
 * This builder is responsible for wiring the configuration loader and
 * augmentations to their respective dependencies.
 * Most methods return 'this', for chaining.
 */
public class ConfigurationBuilder {

    private final JavaDocBuilder builder;

    private ConfigurableListableBeanFactory beanFactory;

    private boolean autowire;

    private boolean withConversions;

    private LoggingKitAdapter logAdapter;

    /**
     * Creates a new ConfigurationBuilder instance.
     */
    public ConfigurationBuilder() {
        this.builder = new JavaDocBuilder();
    }

    /**
     * Creates a new ConfigurationBuilder instance, using the given
     * JavaDocBuilder for querying types in the program.
     */
    public ConfigurationBuilder(JavaDocBuilder builder) {
        this.builder = builder;
    }

    public ConfigurationBuilder addSourceTree(File directory) {
        builder.addSourceTree(directory);
        return this;
    }

    /**
     * Create a BeanFactory for the given resource. The configuration will be
     * built by reading BeanDefinitions from this BeanFactory.
     */
    public ConfigurationBuilder withBeanFactoryOf(Resource resource) {
        assertNoBeanFactory();
        this.beanFactory = new XmlBeanFactory(resource);
        return this;
    }

    /**
     * Sets the ApplicationContext from which bean definitions should be loaded.
     */
    public ConfigurationBuilder withContext(ConfigurableApplicationContext ctxt) {
        assertNoBeanFactory();
        this.beanFactory = ctxt.getBeanFactory();
        return this;
    }

    /**
     * Sets the BeanFactory from which bean definitions should be loaded.
     */
    public ConfigurationBuilder beanFactory(ConfigurableListableBeanFactory beanFactory) {
        assertNoBeanFactory();
        this.beanFactory = beanFactory;
        return this;
    }

    /**
     * Use the autowiring augmentation when building the Configuration.
     */
    public ConfigurationBuilder withAutowiring() {
        this.autowire = true;
        return this;
    }

    /**
     * Use information about injection sites when building the Configuration.
     */
    public ConfigurationBuilder withConversions() {
        this.withConversions = true;
        return this;
    }

    /**
     * Sets the LoggingKitAdapter to use for logging.
     */
    public ConfigurationBuilder withLoggingKitAdapter(LoggingKitAdapter logAdapter) {
        this.logAdapter = logAdapter;
        return this;
    }

    private void assertNoBeanFactory() {
        if (beanFactory != null) {
            throw new RuntimeException("Only one BeanFactory or context can be used.");
        }
    }

    /**
     * Creates a Configuration. This method should be called after a BeanFactory
     * or ApplicationContext has been set.
     */
    public Configuration build() {
        if (beanFactory == null) {
            throw new RuntimeException("BeanFactory has not been set.");
        }
        List<Augmentation> steps = new ArrayList<Augmentation>();
        QDoxAugmentation qdox = new QDoxAugmentation(builder);
        if (logAdapter != null) {
            qdox.setLoggingKitAdapter(logAdapter);
        }
        steps.add(qdox);
        if (autowire) {
            steps.add(new AutowiringAugmentation(builder));
        }
        if (withConversions) {
            steps.add(new SinkAugmentation());
        }
        Augmentation[] augmentations = new Augmentation[steps.size()];
        steps.toArray(augmentations);
        SpringConfigurationLoader loader =
                new SpringConfigurationLoader(beanFactory, augmentations);
        Configuration config = loader.load();
        return config;
    }

}
